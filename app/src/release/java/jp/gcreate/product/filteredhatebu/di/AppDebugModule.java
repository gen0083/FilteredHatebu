package jp.gcreate.product.filteredhatebu.di;

import com.crashlytics.android.core.CrashlyticsCore;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.util.ReleaseLogTree;
import jp.gcreate.product.filteredhatebu.util.StethoWrapper;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppDebugModule {
    @Provides
    @AppScope
    public StethoWrapper provideStethoWrapper() {
        return new StethoWrapper();
    }

    @Provides
    @AppScope
    public Timber.Tree providesTimberTree() {
        return new ReleaseLogTree();
    }

    @Provides
    @AppScope
    public CrashlyticsCore providesCrashlyticsCore() {
        return new CrashlyticsCore.Builder().build();
    }

    @Provides
    @AppScope
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
    }
}
