package jp.gcreate.product.filteredhatebu;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.gcreate.product.filteredhatebu.api.HatebuHotentryCategoryService;
import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.functions.Action1;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedTest {
    private static final String BASE_URL = "http://b.hatena.ne.jp/";
    private HatebuHotentryCategoryService service;

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(HatebuHotentryCategoryService.class);
    }

    @Test
    public void ITカテゴリホッテントリ取得() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        service.getCategoryFeed("it")
                .subscribe(new Action1<HatebuFeed>() {
                    @Override
                    public void call(HatebuFeed hatebuFeed) {
                        for (HatebuFeedItem item: hatebuFeed.getItemList()) {
                            System.out.println(item.toString());
                        }
                        latch.countDown();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("error cause:" + throwable.getCause() + ", message:"
                        + throwable.getMessage());
                        throwable.printStackTrace();
                        latch.countDown();
                    }
                });
        latch.await(30, TimeUnit.SECONDS);
    }
}
