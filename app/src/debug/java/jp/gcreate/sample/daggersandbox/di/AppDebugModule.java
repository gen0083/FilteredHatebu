package jp.gcreate.sample.daggersandbox.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.sample.daggersandbox.di.Scope.AppScope;
import jp.gcreate.sample.daggersandbox.di.qualifier.ApplicationContext;
import jp.gcreate.sample.daggersandbox.util.StethoWrapper;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppDebugModule {
    @Provides
    @AppScope
    public StethoWrapper provideStethoWrapper(@ApplicationContext Context context) {
        return new StethoWrapper(context);
    }

    @Provides
    @AppScope
    public Timber.Tree providesTimberTree() {
        return new Timber.DebugTree();
    }
}
