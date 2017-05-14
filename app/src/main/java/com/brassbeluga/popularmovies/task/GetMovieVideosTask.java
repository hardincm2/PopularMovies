package com.brassbeluga.popularmovies.task;

import android.os.AsyncTask;
import android.util.Log;

import com.brassbeluga.popularmovies.listener.UpdatedMovieVideosListener;
import com.brassbeluga.popularmovies.model.MovieInfoResponse;
import com.brassbeluga.popularmovies.model.MovieVideosResponse;
import com.brassbeluga.popularmovies.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import lombok.Data;

/**
 * Asynchronous task to fetch movie vidoes on a background thread.
 */
public class GetMovieVideosTask extends AsyncTask<GetMovieVideosTask.TaskRequestInput, Void, String> {
    private static final String TAG = GetMovieInfoTask.class.getSimpleName();

    private GetMovieVideosTask.TaskRequestInput input;

    @Inject
    public GetMovieVideosTask() {}

    @Override
    protected String doInBackground(GetMovieVideosTask.TaskRequestInput... requestUrl) {
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
            MovieVideosResponse movieVideosResponse = new Gson().fromJson(jsonResponse, MovieVideosResponse.class);
            input.getListener().movieVideosUpdated(movieVideosResponse);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "Error reading JSON response", ex);
        }
    }

    @Data
    public static class TaskRequestInput {
        private URL targetUrl;
        private UpdatedMovieVideosListener listener;
    }
}
