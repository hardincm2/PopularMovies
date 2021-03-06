package com.brassbeluga.popularmovies;

import android.app.Activity;
import android.app.Application;

import com.brassbeluga.popularmovies.component.DaggerMovieApplicationComponent;
import com.brassbeluga.popularmovies.module.ApplicationModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Custom {@link Application} for this movie app. This enables custom Activity injection.
 */
public class MovieApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerMovieApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}