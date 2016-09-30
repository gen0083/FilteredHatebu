package jp.gcreate.sample.daggersandbox;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import jp.gcreate.sample.daggersandbox.api.HatebuService;
import jp.gcreate.sample.daggersandbox.model.HatebuEntry;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
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
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        okhttp3.Response response = chain.proceed(request);
                        String           rawJson  = response.body().string();

                        System.out.println("request:" + request);
                        System.out.println(String.format("raw JSON response is: %s", rawJson));

                        return response.newBuilder().body(
                                ResponseBody.create(response.body().contentType(), rawJson)).build();
                    }
                })
                .addNetworkInterceptor(new HttpLoggingInterceptor())
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
