package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import jp.gcreate.product.filteredhatebu.util.ReleaseLogTree;
import jp.gcreate.product.filteredhatebu.util.StethoWrapper;
import okhttp3.OkHttpClient;
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
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
    }

    @Provides
    @AppScope
    public Picasso.Builder providePicassoBuilder(@ApplicationContext Context context,
                                                 OkHttpClient client) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .loggingEnabled(true)
                .indicatorsEnabled(true);
    }
}
