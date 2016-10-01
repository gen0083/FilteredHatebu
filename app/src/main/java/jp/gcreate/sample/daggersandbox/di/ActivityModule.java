package jp.gcreate.sample.daggersandbox.di;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.sample.daggersandbox.di.Scope.ActivityScope;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class ActivityModule {

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
