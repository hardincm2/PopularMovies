package com.brassbeluga.popularmovies.component;

import com.brassbeluga.popularmovies.MovieApplication;
import com.brassbeluga.popularmovies.module.ApplicationModule;
import com.brassbeluga.popularmovies.module.NetworkModule;
import com.brassbeluga.popularmovies.module.ServiceModule;
import com.brassbeluga.popularmovies.module.activity.MainActivityModule;
import com.brassbeluga.popularmovies.module.activity.MovieDetailActivityModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        ApplicationModule.class,
        MainActivityModule.class,
        MovieDetailActivityModule.class,
        ServiceModule.class,
        NetworkModule.class})
public interface MovieApplicationComponent {
    void inject(MovieApplication application);
}
