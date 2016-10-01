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
    private Context activityContext;

    public ActivityModule(Context activityContext) {
        this.activityContext = activityContext;
    }

    public void updateContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    @Provides
    @Named("activity")
    public Context provideActivityContext() {
        return activityContext;
    }

    @Provides
    @ActivityScope
    public String provideStringTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Provides
    public long provideTime() {
        return System.currentTimeMillis();
    }
}
