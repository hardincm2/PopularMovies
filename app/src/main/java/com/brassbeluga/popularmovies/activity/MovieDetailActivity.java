package com.brassbeluga.popularmovies.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brassbeluga.popularmovies.R;
import com.brassbeluga.popularmovies.data.MovieDbDao;
import com.brassbeluga.popularmovies.data.model.MovieInfoDto;
import com.brassbeluga.popularmovies.listener.UpdatedMovieDataListener;
import com.brassbeluga.popularmovies.model.MovieDetailsResponse;
import com.brassbeluga.popularmovies.model.MovieInfo;
import com.brassbeluga.popularmovies.model.MovieReview;
import com.brassbeluga.popularmovies.model.MovieReviewsResponse;
import com.brassbeluga.popularmovies.model.MovieVideo;
import com.brassbeluga.popularmovies.model.MovieVideosResponse;
import com.brassbeluga.popularmovies.service.MovieDbService;
import com.brassbeluga.popularmovies.util.DateFormatUtils;
import com.brassbeluga.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

import static android.view.View.GONE;
import static com.brassbeluga.popularmovies.contants.ColorConstants.FAVORITED_COLOR_FILTER;
import static com.brassbeluga.popularmovies.contants.ColorConstants.AUTHOR_COLOR_CODES;
import static com.brassbeluga.popularmovies.contants.ExtraDataKeys.MOVIE_INFO_EXTRA_DATA;
import static com.brassbeluga.popularmovies.service.MovieDbService.BASE_IMAGE_URL;

/**
 * Activity responsible for the movie detail view created
 * after selecting a movie from the MainActivity
 */
public class MovieDetailActivity extends AppCompatActivity implements UpdatedMovieDataListener {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
    private static final String VIDEO_QUERY_KEY = "v";

    @Inject MovieDbService movieDbService;
    @Inject MovieDbDao movieDbDao;

    @BindView(R.id.my_toolbar) Toolbar myToolbar;
    @BindView(R.id.tv_movie_title) TextView movieTitleTextView;
    @BindView(R.id.movie_videos_container) ViewGroup movieVideoContainer;
    @BindView(R.id.movie_reviews_container) ViewGroup movieReviewContainer;
    @BindView(R.id.tv_movie_overview) TextView movieOverviewTextView;
    @BindView(R.id.tv_movie_duration) TextView movieDurationTextView;
    @BindView(R.id.iv_movie_detail_poster) ImageView movieDetailImagePoster;
    @BindView(R.id.tv_movie_release_year) TextView movieReleaseYearTextView;
    @BindView(R.id.tv_movie_rating) TextView movieRatingTextView;
    @BindView(R.id.btn_favorite_movie) Button favoriteMovieButton;
    @BindView(R.id.pb_video_trailers) ProgressBar movieTrailersProgressBar;
    @BindView(R.id.pb_movie_reviews) ProgressBar movieReviewsProgressBar;

    private MovieInfo movieInfo;
    private MovieDetailsResponse movieDetailsResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // Configure the action bar for this activity.
        setSupportActionBar(myToolbar);

        // This will allow a back button to appear next to the title to
        // return back to the parent Activity.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(MOVIE_INFO_EXTRA_DATA)) {
            movieInfo = (MovieInfo) intent.getSerializableExtra(MOVIE_INFO_EXTRA_DATA);
        } else {
            // Return to parent activity
            Log.e(TAG, "No movie info was provided to MovieDetailsActivity");
            NavUtils.navigateUpFromSameTask(this);
            return;
        }

        movieTitleTextView.setText(movieInfo.title);
        movieOverviewTextView.setText(movieInfo.overview);
        Picasso.with(this.getApplicationContext()).load(BASE_IMAGE_URL + movieInfo.poster_path).into(movieDetailImagePoster);

        int year = DateFormatUtils.getYear("yyyy-MM-dd", movieInfo.release_date);
        movieReleaseYearTextView.setText(Integer.toString(year));

        String movieRatingString = getString(R.string.movie_detail_rating_formattable);
        movieRatingString = String.format(movieRatingString, Double.toString(movieInfo.vote_average));
        movieRatingTextView.setText(movieRatingString);

        // Trigger the movie details, videos and reviews fetch requests
        movieDbService.getMovieDetails(this, movieInfo.id);
        movieDbService.getMovieVideos(this, movieInfo.id);
        movieDbService.getMovieReviews(this, movieInfo.id);

        if (movieDbDao.isFavoriteMovie(movieInfo.id)) {
            favoriteMovieButton.getBackground().setColorFilter(FAVORITED_COLOR_FILTER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings :
                Toast settingsToast = Toast.makeText(this, R.string.action_settings_toast_message, Toast.LENGTH_SHORT);
                settingsToast.show();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * To be triggered every time the favorite button is pressed.
     */
    public void onFavoriteButtonPressed(View view) {
        if (!movieDbDao.isFavoriteMovie(movieInfo.id)) {
            // This movie is currently unfavorited, so lets store this movie info
            MovieInfoDto movieInfoDto = MovieInfoDto.builder()
                    .overview(movieInfo.overview)
                    .posterImagePath(movieInfo.poster_path)
                    .rating(movieInfo.vote_average)
                    .releaseDate(movieInfo.release_date)
                    .title(movieInfo.title)
                    .runtime(movieDetailsResponse.runtime)
                    .movieId(movieInfo.id)
                    .build();

            movieDbDao.writeFavoriteMovie(movieInfoDto);
            favoriteMovieButton.getBackground().setColorFilter(FAVORITED_COLOR_FILTER);
        } else {
            // This movie is already favorited... so unfavorite it by deleting the entry in our db.
            movieDbDao.deleteFavoriteMovie(movieInfo.id);
            favoriteMovieButton.getBackground().setColorFilter(null);
        }

    }

    @Override
    public void movieDataUpdated(Object movieDataResponse) {
        if (movieDataResponse instanceof MovieDetailsResponse) {
            onMovieDetailsUpdated((MovieDetailsResponse) movieDataResponse);
        } else if (movieDataResponse instanceof MovieVideosResponse) {
            onMovieVideosUpdated((MovieVideosResponse) movieDataResponse);
        } else if (movieDataResponse instanceof MovieReviewsResponse) {
            onMovieReviewsUpdated((MovieReviewsResponse) movieDataResponse);
        }
    }

    private void onMovieDetailsUpdated(MovieDetailsResponse movieDetailsResponse) {
        this.movieDetailsResponse = movieDetailsResponse;
        // Format the duration String and the set the text on the text view.
        String movieDurationString = getString(R.string.movie_detail_runtime_formattable);
        movieDurationString = String.format(movieDurationString, Long.toString(movieDetailsResponse.runtime));
        movieDurationTextView.setText(movieDurationString);
    }

    private void onMovieVideosUpdated(MovieVideosResponse movieVideosResponse) {
        // Hide the progress bar since the fetch has completed
        movieTrailersProgressBar.setVisibility(GONE);

        LayoutInflater layoutInflater = getLayoutInflater();
        int trailerCount = 0;
        for (MovieVideo movieVideo : movieVideosResponse.results) {
            if (movieVideo.type.equals("Trailer")) {
                // Inflate trailer item view holder.
                View trailerViewHolder = layoutInflater.inflate(R.layout.movie_trailer_item, null);

                // Find the inner text view and set the text along with a tag containing the key needed
                // to fetch the video url.
                TextView tvMovieTrailer = (TextView) trailerViewHolder.findViewById(R.id.tv_movie_trailer);
                tvMovieTrailer.setText(String.format(getString(R.string.movie_trailer_index_label), Integer.toString(++trailerCount)));
                trailerViewHolder.setTag(movieVideo.key);
                trailerViewHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open the youtube url through an implicit intent.
                        URL url = NetworkUtils.buildUrl(BASE_YOUTUBE_URL, VIDEO_QUERY_KEY, (String) v.getTag());
                        Uri uri = Uri.parse(url.toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                // Append this to the list of trailer views
                movieVideoContainer.addView(trailerViewHolder);
            }
        }
    }

    private void onMovieReviewsUpdated(MovieReviewsResponse movieReviewsResponse) {
        movieReviewsProgressBar.setVisibility(GONE);
        LayoutInflater layoutInflater = getLayoutInflater();

        int reviewCount = 0;
        for (MovieReview movieReview : movieReviewsResponse.results) {
            View reviewViewHolder = layoutInflater.inflate(R.layout.movie_review_item, null);

            TextView tvMovieReviewContent = (TextView) reviewViewHolder.findViewById(R.id.tv_movie_review_content);
            TextView tvMovieReviewAuthor = (TextView) reviewViewHolder.findViewById(R.id.tv_movie_review_author);
            ImageView ivAuthorIcon = (ImageView) reviewViewHolder.findViewById(R.id.iv_author_icon);

            tvMovieReviewContent.setText(movieReview.content);

            // Make it so when the user clicks on the content of a review, it will collapse/expand.
            tvMovieReviewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v;

                    if (textView.getMaxLines() == Integer.MAX_VALUE) {
                        textView.setMaxLines(3);
                    } else {
                        textView.setMaxLines(Integer.MAX_VALUE);
                    }

                }
            });

            tvMovieReviewAuthor.setText(movieReview.author);


            // We will color each author review icon a different color :)
            int colorCodeIndex = reviewCount++ % AUTHOR_COLOR_CODES.length;
            ivAuthorIcon.setColorFilter(AUTHOR_COLOR_CODES[colorCodeIndex], PorterDuff.Mode.MULTIPLY);

            // Attache the review to the parent container so it is visible to the user.
            movieReviewContainer.addView(reviewViewHolder);
        }
    }
}
