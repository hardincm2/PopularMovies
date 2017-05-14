package com.brassbeluga.popularmovies.module;


import com.brassbeluga.popularmovies.service.MovieDbService;
import com.brassbeluga.popularmovies.task.GetMovieDetailsTask;
import com.brassbeluga.popularmovies.task.GetMovieInfoTask;
import com.brassbeluga.popularmovies.task.GetMovieVideosTask;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    public static MovieDbService provideMovieDbService(Provider<GetMovieInfoTask> getMovieInfoTaskProvider,
                                                       Provider<GetMovieDetailsTask> getMovieDetailsTaskProvider,
                                                       Provider<GetMovieVideosTask> getMovieVideosTaskProvider) {
        return new MovieDbService(getMovieInfoTaskProvider, getMovieDetailsTaskProvider, getMovieVideosTaskProvider);
    }
}
