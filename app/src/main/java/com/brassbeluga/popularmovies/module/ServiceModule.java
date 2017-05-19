package com.brassbeluga.popularmovies.module;


import com.brassbeluga.popularmovies.service.MovieDbService;
import com.brassbeluga.popularmovies.task.GetMovieDataTask;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    public static MovieDbService provideMovieDbService(Provider<GetMovieDataTask> getMovieDataTaskProvider) {
        return new MovieDbService(getMovieDataTaskProvider);
    }
}
