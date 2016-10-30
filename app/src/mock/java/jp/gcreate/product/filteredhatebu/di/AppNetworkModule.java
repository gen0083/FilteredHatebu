package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet;
import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.api.MockInterceptor;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppNetworkModule {

    @Provides
    @AppScope
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context,
                                            HttpLoggingInterceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new MockInterceptor(context));
        return builder.build();
    }

    @Provides
    @AppScope
    public FeedsBurnerClienet provideHatebuHotentryService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedsBurnerClienet.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(FeedsBurnerClienet.class);
    }

    @Provides
    @AppScope
    public HatenaClient.JsonService provideHatebuJsonService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HatenaClient.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatenaClient.JsonService.class);
    }

    @Provides
    @AppScope
    public HatenaClient.XmlService provideHatebuXmlService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HatenaClient.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HatenaClient.XmlService.class);
    }
}
