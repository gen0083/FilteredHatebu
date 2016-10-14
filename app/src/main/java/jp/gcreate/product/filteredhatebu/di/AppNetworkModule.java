package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.api.HatebuFeedService;
import jp.gcreate.product.filteredhatebu.api.HatebuService;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppNetworkModule {
    private static final String HATEBU_BASE_URL = "https://b.hatena.ne.jp/";
    private static final String OKHTTP_CACHE_DIR = "okhttp";
    private static final int OKHTTP_CACHE_SIZE = 4 * 1024 * 1024;

    @Provides
    @AppScope
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
        File  cacheDir = new File(context.getCacheDir(), OKHTTP_CACHE_DIR);
        Cache cache    = new Cache(cacheDir, OKHTTP_CACHE_SIZE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache);
        return builder.build();
    }

    @Provides
    @AppScope
    public HatebuFeedService provideHatebuFeedService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HATEBU_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatebuFeedService.class);
    }

    @Provides
    @AppScope
    public HatebuService provideHatebuService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HATEBU_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatebuService.class);
    }
}
