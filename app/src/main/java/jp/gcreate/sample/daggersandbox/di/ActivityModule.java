package jp.gcreate.sample.daggersandbox.di;

import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.sample.daggersandbox.di.Scope.ActivityScope;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class ActivityModule {
    private Context context;

    public ActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    @ActivityScope
    @Named("activity")
    public Context provideActivityContext() {
        return context;
    }

    @Provides
    @ActivityScope
    public String provideStringTime() {
        return String.valueOf(System.currentTimeMillis());
    }
}
