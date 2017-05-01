package com.brassbeluga.popularmovies.module.activity;


import android.app.Activity;

import com.brassbeluga.popularmovies.activity.MovieDetailActivity;
import com.brassbeluga.popularmovies.component.subcomponent.MovieDetailActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = MovieDetailActivitySubcomponent.class)
public abstract class MovieDetailActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MovieDetailActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindYourActivityInjectorFactory(MovieDetailActivitySubcomponent.Builder builder);
}
