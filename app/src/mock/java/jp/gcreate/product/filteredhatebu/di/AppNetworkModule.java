package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.api.HatebuEntryService;
import jp.gcreate.product.filteredhatebu.api.HatebuHotentryCategoryService;
import jp.gcreate.product.filteredhatebu.api.HatebuHotentryService;
import jp.gcreate.product.filteredhatebu.api.MockInterceptor;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
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
    public static final String HATEBU_BASE_URL     = "https://b.hatena.ne.jp/";
    public static final String FEEDBURNER_BASE_URL = "http://feeds.feedburner.com/";

    @Provides
    @AppScope
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                             .addInterceptor(new MockInterceptor(context));
        return builder.build();
    }

    @Provides
    @AppScope
    public HatebuHotentryService provideHatebuHotentryService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FEEDBURNER_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatebuHotentryService.class);
    }

    @Provides
    @AppScope
    public HatebuHotentryCategoryService provideHatebuFeedService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HATEBU_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatebuHotentryCategoryService.class);
    }

    @Provides
    @AppScope
    public HatebuEntryService provideHatebuService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HATEBU_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatebuEntryService.class);
    }
}
