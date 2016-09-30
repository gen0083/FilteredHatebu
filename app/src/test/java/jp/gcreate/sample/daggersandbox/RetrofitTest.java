package jp.gcreate.sample.daggersandbox;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import jp.gcreate.sample.daggersandbox.api.HatebuService;
import jp.gcreate.sample.daggersandbox.model.HatebuEntry;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Copyright 2016 G-CREATE
 */

public class RetrofitTest {
    private Retrofit      retrofit;
    private HatebuService service;
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
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(HatebuService.class);
    }

    @Test
    public void test_call() throws IOException, InterruptedException {
        final CountDownLatch latch     = new CountDownLatch(1);
        Call<HatebuEntry>    entryCall = service
                .getEntry("http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getinfo");
        Callback<HatebuEntry> callback = new Callback<HatebuEntry>() {
            @Override
            public void onResponse(Call<HatebuEntry> call, Response<HatebuEntry> response) {
                System.out.println(response.body());
                latch.countDown();
            }

            @Override
            public void onFailure(Call<HatebuEntry> call, Throwable t) {
                latch.countDown();
            }
        };
        HatebuEntry response = entryCall.execute().body();
        System.out.println(response);
    }
}
