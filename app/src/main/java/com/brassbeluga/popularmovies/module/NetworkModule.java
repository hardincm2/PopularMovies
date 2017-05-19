package com.brassbeluga.popularmovies.module;

import com.brassbeluga.popularmovies.task.GetMovieDataTask;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {
    @Provides
    public static GetMovieDataTask provideGetMovieDataTask() {
        return new GetMovieDataTask();
    }
}
