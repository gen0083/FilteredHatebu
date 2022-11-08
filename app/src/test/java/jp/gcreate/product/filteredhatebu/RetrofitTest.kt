package jp.gcreate.product.filteredhatebu;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.model.HatebuEntry;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.Observable;
import rx.functions.Action1;

/**
 * Copyright 2016 G-CREATE
 */

public class RetrofitTest {
    private Retrofit     retrofit;
    private HatenaClient.JsonService service;
    private String BASE_URL = "http://b.hatena.ne.jp/";

    @Before
    public void setUp() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        System.out.println(message);
                    }
                })
                .setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(logger)
                .addInterceptor(logger)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(HatenaClient.JsonService.class);
    }

    @Test
    public void test_call() throws IOException, InterruptedException {
        final CountDownLatch latch     = new CountDownLatch(1);
        Observable<HatebuEntry> entryCall = service
                .getEntry("http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getinfo");
        entryCall.subscribe(new Action1<HatebuEntry>() {
            @Override
            public void call(HatebuEntry hatebuEntry) {
                System.out.println("gotten entry:" + hatebuEntry);
                System.out.println("bookmarks:" + hatebuEntry.getBookmarks().size());
                for (HatebuBookmark bookmark : hatebuEntry.getBookmarks()) {
                    System.out.println(bookmark);
                }
                latch.countDown();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println("error");
                latch.countDown();
            }
        });
        latch.await(30, TimeUnit.SECONDS);
    }
}
