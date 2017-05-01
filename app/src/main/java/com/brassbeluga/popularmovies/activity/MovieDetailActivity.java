package com.brassbeluga.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brassbeluga.popularmovies.R;
import com.brassbeluga.popularmovies.listener.UpdatedMovieDetailsListener;
import com.brassbeluga.popularmovies.model.MovieDetails;
import com.brassbeluga.popularmovies.model.MovieInfo;
import com.brassbeluga.popularmovies.service.MovieDbService;
import com.brassbeluga.popularmovies.util.DateFormatUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.brassbeluga.popularmovies.contants.ExtraDataKeys.MOVIE_INFO_EXTRA_DATA;
import static com.brassbeluga.popularmovies.service.MovieDbService.BASE_IMAGE_URL;

/**
 * Activity responsible for the movie detail view created
 * after selecting a movie from the MainActivity
 */
public class MovieDetailActivity extends AppCompatActivity implements UpdatedMovieDetailsListener {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Inject
    MovieDbService movieDbService;

    private MovieInfo movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Configure the action bar for this activity.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
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

        TextView movieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        movieTitleTextView.setText(movieInfo.title);

        TextView movieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        movieOverviewTextView.setText(movieInfo.overview);

        ImageView movieDetailImagePoster = (ImageView) findViewById(R.id.iv_movie_detail_poster);
        Picasso.with(this.getApplicationContext()).load(BASE_IMAGE_URL + movieInfo.poster_path).into(movieDetailImagePoster);

        int year = DateFormatUtils.getYear("yyyy-MM-dd", movieInfo.release_date);
        TextView movieReleaseYearTextView = (TextView) findViewById(R.id.tv_movie_release_year);
        movieReleaseYearTextView.setText(Integer.toString(year));

        String movieRatingString = getString(R.string.movie_detail_rating_formattable);
        movieRatingString = String.format(movieRatingString, Double.toString(movieInfo.vote_average));
        TextView movieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        movieRatingTextView.setText(movieRatingString);

        movieDbService.getMovieDetails(this, movieInfo.id);
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

    @Override
    public void movieDetailsUpdated(MovieDetails movieDetails) {
        // Format the duration String and the set the text on the text view.
        String movieDurationString = getString(R.string.movie_detail_runtime_formattable);
        movieDurationString = String.format(movieDurationString, Long.toString(movieDetails.runtime));
        TextView movieDurationTextView = (TextView) findViewById(R.id.tv_movie_duration);
        movieDurationTextView.setText(movieDurationString);
    }
}
