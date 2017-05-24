package com.brassbeluga.popularmovies.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.brassbeluga.popularmovies.R;
import com.brassbeluga.popularmovies.adapter.MovieViewRecyclerAdapter;
import com.brassbeluga.popularmovies.data.MovieDbDao;
import com.brassbeluga.popularmovies.data.model.MovieInfoDto;
import com.brassbeluga.popularmovies.listener.UpdatedMovieDataListener;
import com.brassbeluga.popularmovies.model.MovieFilter;
import com.brassbeluga.popularmovies.model.MovieInfo;
import com.brassbeluga.popularmovies.model.MovieInfoResponse;
import com.brassbeluga.popularmovies.service.MovieDbService;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

import static com.brassbeluga.popularmovies.model.MovieFilter.FAVORITE;
import static com.brassbeluga.popularmovies.model.MovieFilter.POPULAR;
import static com.brassbeluga.popularmovies.model.MovieFilter.TOP_RATED;
import static com.brassbeluga.popularmovies.util.DisplayUtils.getRecyclerViewSpanCount;

public class MainActivity extends AppCompatActivity implements UpdatedMovieDataListener {

    @Inject MovieDbService movieDbService;
    @Inject MovieDbDao movieDbDao;

    @BindView(R.id.rv_movies_list) RecyclerView moviesListView;
    @BindView(R.id.my_toolbar) Toolbar myToolbar;

    private MovieViewRecyclerAdapter movieViewAdapter;
    private MovieFilter currentMovieFilter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Configure the action bar for the main activity.
        setSupportActionBar(myToolbar);

        // Generate the span count
        int spanCount = getRecyclerViewSpanCount(getResources().getConfiguration());

        // Prepare the grid recycler view that will house the movie images
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        moviesListView.setLayoutManager(gridLayoutManager);
        movieViewAdapter = new MovieViewRecyclerAdapter();
        moviesListView.setAdapter(movieViewAdapter);

        final MainActivity mainActivity = this;
        moviesListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean atBottomOfScrollView = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int itemCount = gridLayoutManager.getItemCount(); // Total number of items in the grid
                int spanCount = gridLayoutManager.getSpanCount(); // Number of items per grid span (a single row)

                // If the last visible item, is within TWO grid spans from the end of the scroll view, we will
                // begin a fetch for additional movie information instead of waiting until the user to
                // actually gets all the way to the end of the scroll view. This prefetch leads to a smoother
                // endless scrolling CX.
                if(layoutManager.findLastVisibleItemPosition() >= itemCount - spanCount * 2 - 1){
                    if (!atBottomOfScrollView) {
                        atBottomOfScrollView = true;

                        if (currentMovieFilter != FAVORITE) {
                            movieDbService.getMovieInfo(mainActivity, currentMovieFilter, itemCount);
                        }
                    }
                } else {
                    atBottomOfScrollView = false;
                }
            }
        });

        // Start off by getting popular movies
        currentMovieFilter = MovieFilter.POPULAR;
        movieDbService.getMovieInfo(this, currentMovieFilter, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentMovieFilter == FAVORITE) {
            updateAndDisplayFavoriteMovies();

            if (movieViewAdapter.getItemCount() == 0) {
                // No favorite movies are left, go back to popular movies default.
                currentMovieFilter = MovieFilter.POPULAR;
                movieViewAdapter.setMovieInfos(null);
                movieDbService.getMovieInfo(this, currentMovieFilter, 0);
                menu.findItem(R.id.menu_sort_popular).setChecked(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;

        // Since sorted by popular movies is our default, we will check this
        // menu items radio button upon menu creation.
        menu.findItem(R.id.menu_sort_popular).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popular :
                item.setChecked(true);
                if (currentMovieFilter != POPULAR) {
                    currentMovieFilter = MovieFilter.POPULAR;
                    movieViewAdapter.setMovieInfos(null);
                    movieDbService.getMovieInfo(this, currentMovieFilter, 0);
                } // Else the user is selecting filter by popular when we are already displaying popular movies.
                return true;
            case R.id.menu_sort_top_rated :
                item.setChecked(true);
                if (currentMovieFilter != TOP_RATED) {
                    currentMovieFilter = MovieFilter.TOP_RATED;
                    movieViewAdapter.setMovieInfos(null);
                    movieDbService.getMovieInfo(this, currentMovieFilter, 0);
                } // Else the user is selecting filter by top rated when we are already displaying top rated movies.
                return true;
            case R.id.menu_sort_favorites :
                if (currentMovieFilter != FAVORITE) {
                    boolean favoriteMoviesDisplayed = updateAndDisplayFavoriteMovies();
                    if (favoriteMoviesDisplayed) {
                        item.setChecked(true);
                    }
                }
                return true;
            case R.id.action_settings :
                Toast settingsToast = Toast.makeText(this, R.string.action_settings_toast_message, Toast.LENGTH_SHORT);
                settingsToast.show();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void movieDataUpdated(Object movieDataResponse) {
        if (movieDataResponse instanceof MovieInfoResponse) {
            MovieInfoResponse movieInfoResponse = (MovieInfoResponse) movieDataResponse;

            if (movieViewAdapter.getMovieInfos() != null) {
                // There are already some movies in the view so we need to append the recent movie info
                // data with existing.
                MovieInfo[] oldMovieInfos = movieViewAdapter.getMovieInfos();
                MovieInfo[] newMovieInfos = movieInfoResponse.results;
                MovieInfo[] allMovieInfos = ArrayUtils.addAll(oldMovieInfos, newMovieInfos);
                movieViewAdapter.setMovieInfos(allMovieInfos);
            } else {
                // No existing movie data is set so we can just set it to our results in the view
                movieViewAdapter.setMovieInfos(movieInfoResponse.results);
            }

            // Make sure the adapter is aware that we have modified the underlying data.
            movieViewAdapter.notifyDataSetChanged();
        }
    }

    private boolean updateAndDisplayFavoriteMovies() {
        List<MovieInfoDto> movieInfoDtos = movieDbDao.readFavoriteMovies();
        List<MovieInfo> movieInfos = new ArrayList<>();
        boolean favoriteMoviesDisplayed = false;
        for (MovieInfoDto movieInfoDto : movieInfoDtos) {
            movieInfos.add(movieInfoDto.toMovieInfo());
        }
        if (movieInfos.isEmpty()) {
            movieViewAdapter.setMovieInfos(null);
            movieViewAdapter.notifyDataSetChanged();
            // The user hasn't favorited any movies yet, so we will not update the movies list and instead will
            // just show a toast to inform them.
            Toast settingsToast = Toast.makeText(this, R.string.toast_no_favorites, Toast.LENGTH_SHORT);
            settingsToast.show();
        } else {
            favoriteMoviesDisplayed = true;
            currentMovieFilter = MovieFilter.FAVORITE;
            movieViewAdapter.setMovieInfos(movieInfos.toArray(new MovieInfo[movieInfos.size()]));
            movieViewAdapter.notifyDataSetChanged();
        }

        return favoriteMoviesDisplayed;
    }
}
