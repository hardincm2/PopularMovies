package com.brassbeluga.popularmovies.module;

import com.brassbeluga.popularmovies.task.GetMovieDetailsTask;
import com.brassbeluga.popularmovies.task.GetMovieInfoTask;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Provides
    public static GetMovieInfoTask provideGetMovieInfoTask() {
        return new GetMovieInfoTask();
    }

    @Provides
    public static GetMovieDetailsTask provideGetMovieDetailsTask() {
        return new GetMovieDetailsTask();
    }
}
