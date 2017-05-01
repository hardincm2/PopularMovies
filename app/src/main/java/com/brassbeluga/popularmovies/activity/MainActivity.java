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
import com.brassbeluga.popularmovies.listener.UpdatedMovieInfoListener;
import com.brassbeluga.popularmovies.model.MovieFilter;
import com.brassbeluga.popularmovies.model.MovieInfo;
import com.brassbeluga.popularmovies.model.MovieInfoResponse;
import com.brassbeluga.popularmovies.service.MovieDbService;

import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.brassbeluga.popularmovies.model.MovieFilter.POPULAR;
import static com.brassbeluga.popularmovies.model.MovieFilter.TOP_RATED;

public class MainActivity extends AppCompatActivity implements UpdatedMovieInfoListener {

    @Inject
    MovieDbService movieDbService;

    private RecyclerView moviesListView;
    private MovieViewRecyclerAdapter movieViewAdapter;
    private MovieFilter currentMovieFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure the action bar for the main activity.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Prepare the grid recycler view that will house the movie images
        moviesListView = (RecyclerView) findViewById(R.id.rv_movies_list);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
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
                        movieDbService.getMovieInfo(mainActivity, currentMovieFilter, itemCount);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

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
            case R.id.action_settings :
                Toast settingsToast = Toast.makeText(this, R.string.action_settings_toast_message, Toast.LENGTH_SHORT);
                settingsToast.show();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void movieInfoUpdated(MovieInfoResponse movieInfoResponse) {
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
