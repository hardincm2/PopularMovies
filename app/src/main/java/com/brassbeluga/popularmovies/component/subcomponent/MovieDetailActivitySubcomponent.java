package com.brassbeluga.popularmovies.component.subcomponent;

import com.brassbeluga.popularmovies.activity.MovieDetailActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface MovieDetailActivitySubcomponent extends AndroidInjector<MovieDetailActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MovieDetailActivity> {}
}