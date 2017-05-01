package com.brassbeluga.popularmovies.task;

import android.os.AsyncTask;
import android.util.Log;

import com.brassbeluga.popularmovies.listener.UpdatedMovieDetailsListener;
import com.brassbeluga.popularmovies.listener.UpdatedMovieInfoListener;
import com.brassbeluga.popularmovies.model.MovieDetails;
import com.brassbeluga.popularmovies.model.MovieInfoResponse;
import com.brassbeluga.popularmovies.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import lombok.Data;

/**
 * Asynchronous task to fetch movie details on a background thread.
 */
public class GetMovieDetailsTask extends AsyncTask<GetMovieDetailsTask.TaskRequestInput, Void, String> {
    private static final String TAG = GetMovieInfoTask.class.getSimpleName();

    private TaskRequestInput input;

    @Inject
    public GetMovieDetailsTask() {}

    @Override
    protected String doInBackground(GetMovieDetailsTask.TaskRequestInput... requestUrl) {
        input = requestUrl[0];
        try {
            return NetworkUtils.getResponseFromHttpUrl(input.getTargetUrl());
        } catch (IOException ex) {
            Log.e(TAG, String.format("Unable to read response from: %s", input.getTargetUrl().getPath()));
            return null;
        }
    }

    @Override
    protected void onPostExecute(String jsonResponse) {
        super.onPostExecute(jsonResponse);
        try {
            MovieDetails movieDetailsResponse = new Gson().fromJson(jsonResponse, MovieDetails.class);
            input.getListener().movieDetailsUpdated(movieDetailsResponse);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "Error reading JSON response", ex);
        }
    }

    @Data
    public static class TaskRequestInput {
        private URL targetUrl;
        private UpdatedMovieDetailsListener listener;
    }
}
