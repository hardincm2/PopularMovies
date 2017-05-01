package com.brassbeluga.popularmovies.module.activity;


import android.app.Activity;

import com.brassbeluga.popularmovies.activity.MainActivity;
import com.brassbeluga.popularmovies.component.subcomponent.MainActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = MainActivitySubcomponent.class)
public abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindYourActivityInjectorFactory(MainActivitySubcomponent.Builder builder);
}
