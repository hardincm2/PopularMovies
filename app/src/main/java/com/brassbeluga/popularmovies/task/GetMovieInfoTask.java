package com.brassbeluga.popularmovies.task;

import android.os.AsyncTask;
import android.util.Log;

import com.brassbeluga.popularmovies.listener.UpdatedMovieInfoListener;
import com.brassbeluga.popularmovies.model.MovieInfoResponse;
import com.brassbeluga.popularmovies.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import lombok.Data;

/**
 * Asynchronous task to fetch movie info on a background thread.
 */
public class GetMovieInfoTask extends AsyncTask<GetMovieInfoTask.TaskRequestInput, Void, String> {
    private static final String TAG = GetMovieInfoTask.class.getSimpleName();

    private TaskRequestInput input;

    @Inject
    public GetMovieInfoTask() {}

    @Override
    protected String doInBackground(TaskRequestInput... requestUrl) {
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
            MovieInfoResponse movieInfoResponse = new Gson().fromJson(jsonResponse, MovieInfoResponse.class);
            input.getListener().movieInfoUpdated(movieInfoResponse);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "Error reading JSON response", ex);
        }
    }

    @Data
    public static class TaskRequestInput {
        private URL targetUrl;
        private UpdatedMovieInfoListener listener;
    }
}
