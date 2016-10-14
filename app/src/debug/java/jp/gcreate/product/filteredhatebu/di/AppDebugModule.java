package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import jp.gcreate.product.filteredhatebu.util.StethoWrapper;
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