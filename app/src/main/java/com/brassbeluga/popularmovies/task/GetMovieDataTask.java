package com.brassbeluga.popularmovies.task;

import android.os.AsyncTask;
import android.util.Log;

import com.brassbeluga.popularmovies.listener.UpdatedMovieDataListener;
import com.brassbeluga.popularmovies.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import lombok.Data;

/**
 * Asynchronous task to fetch movie data on a background thread.
 */
public class GetMovieDataTask extends AsyncTask<GetMovieDataTask.TaskRequestInput, Void, String> {
    private static final String TAG = GetMovieDataTask.class.getSimpleName();

    private GetMovieDataTask.TaskRequestInput input;

    @Inject
    public GetMovieDataTask() {}

    @Override
    protected String doInBackground(GetMovieDataTask.TaskRequestInput... requestUrl) {
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
            // Convert the response into the appropriate model provided in the input
            Object movieResponse = new Gson().fromJson(jsonResponse, input.responseModel);
            input.getListener().movieDataUpdated(movieResponse);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, "Error reading JSON response", ex);
        }
    }

    @Data
    public static class TaskRequestInput {
        private URL targetUrl;
        private Class responseModel;
        private UpdatedMovieDataListener listener;
    }
}
