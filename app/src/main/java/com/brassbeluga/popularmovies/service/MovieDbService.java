package com.brassbeluga.popularmovies.service;


import com.brassbeluga.popularmovies.listener.UpdatedMovieDetailsListener;
import com.brassbeluga.popularmovies.listener.UpdatedMovieInfoListener;
import com.brassbeluga.popularmovies.model.MovieFilter;
import com.brassbeluga.popularmovies.task.GetMovieDetailsTask;
import com.brassbeluga.popularmovies.task.GetMovieInfoTask;
import com.brassbeluga.popularmovies.util.NetworkUtils;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provides access to TheMovieDb API. See https://www.themoviedb.org/documentation/api
 */
public class MovieDbService {
    private static final String TAG = MovieDbService.class.getSimpleName();

    public static final String BASE_MOVIE_URL = "https://api.themoviedb.org/3";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String MOVIE_DETAILS_URL = BASE_MOVIE_URL + "/movie/%s";
    public static final String MOVIE_VIDEOS_URL = BASE_MOVIE_URL + "/movie/%s/videos";

    private static final String POPULAR_MOVIE_URL = BASE_MOVIE_URL + "/movie/popular";
    private static final String TOP_RATED_MOVIE_URL = BASE_MOVIE_URL + "/movie/top_rated";

    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = ""; // Set your API key here.

    private static final String PAGE_KEY = "page";

    private static final int RESULTS_PER_PAGE = 20;

    private final Provider<GetMovieInfoTask> getMovieInfoTaskProvider;
    private final Provider<GetMovieDetailsTask> getMovieDetailsTaskProvider;

    @Inject
    public MovieDbService(Provider<GetMovieInfoTask> getMovieInfoTaskProvider,
                          Provider<GetMovieDetailsTask> getMovieDetailsTaskProvider) {
        this.getMovieInfoTaskProvider = getMovieInfoTaskProvider;
        this.getMovieDetailsTaskProvider = getMovieDetailsTaskProvider;
    }

    /**
     * Get movie metadata information from TheMovieDB
     *
     * @param listener Movies response will be passed via callback to this listener
     * @param movieFilter Filter to determine which movie information to fetch
     * @param movieIndex Index of movies to fetch
     */
    public void getMovieInfo(UpdatedMovieInfoListener listener, MovieFilter movieFilter, int movieIndex) {
        // We fetch movie info based on a page number. Each page has a fixed number of results so we
        // use this information to determine which page to grab based on the provided index.
        String page = Integer.toString((movieIndex / RESULTS_PER_PAGE) + 1);

        // Currently only support grabbing either popular or top rated movies.
        String url = movieFilter == MovieFilter.POPULAR ? POPULAR_MOVIE_URL : TOP_RATED_MOVIE_URL;
        URL targetUrl = NetworkUtils.buildUrl(url, API_KEY, API_KEY_VALUE, PAGE_KEY, page);

        // Prepare the task input and thetn execute the GetMovieInfoTask.
        GetMovieInfoTask.TaskRequestInput taskRequestInput = new GetMovieInfoTask.TaskRequestInput();
        taskRequestInput.setTargetUrl(targetUrl);
        taskRequestInput.setListener(listener);
        getMovieInfoTaskProvider.get().execute(taskRequestInput);
    }

    /**
     * Gets details about a specific movie
     *
     * @param listener Movie details response will be passed via callback to this listener
     * @param movieId Unique identifer for the movie being fetched
     */
    public void getMovieDetails(UpdatedMovieDetailsListener listener, long movieId) {
        URL targetUrl = NetworkUtils.buildUrl(String.format(MOVIE_DETAILS_URL, movieId), API_KEY, API_KEY_VALUE);
        GetMovieDetailsTask.TaskRequestInput taskRequestInput = new GetMovieDetailsTask.TaskRequestInput();
        taskRequestInput.setTargetUrl(targetUrl);
        taskRequestInput.setListener(listener);
        getMovieDetailsTaskProvider.get().execute(taskRequestInput);
    }

    public void getMovieVideos(UpdatedMovieDetailsListener listener, long movieId) {
        URL targetUrl = NetworkUtils.buildUrl(String.format(MOVIE_VIDEOS_URL, movieId), API_KEY, API_KEY_VALUE);
        GetMovieDetailsTask.TaskRequestInput taskRequestInput = new GetMovieDetailsTask.TaskRequestInput();
        taskRequestInput.setTargetUrl(targetUrl);
        taskRequestInput.setListener(listener);
        getMovieDetailsTaskProvider.get().execute(taskRequestInput);
    }
}
