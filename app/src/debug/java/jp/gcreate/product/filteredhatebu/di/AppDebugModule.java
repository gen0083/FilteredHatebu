package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
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
    public StethoWrapper provideStethoWrapper(@ApplicationContext Context context) {
        return new StethoWrapper(context);
    }

    @Provides
    @AppScope
    public Timber.Tree providesTimberTree() {
        return new Timber.DebugTree();
    }

    @Provides
    @AppScope
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.tag("OkHttp").v(message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);
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
