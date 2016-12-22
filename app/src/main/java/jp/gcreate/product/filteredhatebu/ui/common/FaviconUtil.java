package jp.gcreate.product.filteredhatebu.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

@AppScope
public class FaviconUtil {
    public static final String FAVICON_URL = "https://favicon.hatena.ne.jp/?url=";
    private OkHttpClient client;
    private Resources resources;
    private HashMap<String, Drawable> memoryCache = new HashMap<>();
    private Drawable placeHolder;
    private int    width;
    private int    height;

    @Inject
    public FaviconUtil(OkHttpClient client, @ApplicationContext Context context) {
        this.client = client;
        placeHolder = ContextCompat.getDrawable(context, R.drawable.favicon_placeholder);
        width = placeHolder.getIntrinsicWidth();
        height = placeHolder.getIntrinsicHeight();
        resources = context.getResources();
    }

    public Observable<Drawable> fetchFavicon(String url) {
        final String domain = substringUntilDomain(url);
        return Observable.fromEmitter(new Action1<Emitter<Drawable>>() {
            @Override
            public void call(final Emitter<Drawable> subscriber) {
                final Request request = new Request.Builder()
                        .url(FAVICON_URL + domain)
                        .build();
                if (memoryCache.get(domain) != null) {
                    subscriber.onNext(memoryCache.get(domain));
                    subscriber.onCompleted();
                    return;
                }
                subscriber.onNext(placeHolder);
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            Bitmap bitmap = BitmapFactory
                                    .decodeStream(response.body().byteStream());
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, false);
                            BitmapDrawable drawable = new BitmapDrawable(resources, scaled);
                            memoryCache.put(domain, drawable);
                            subscriber.onNext(drawable);
                            subscriber.onCompleted();
                        }catch (Exception e) {
                            Timber.e(e);
                            memoryCache.put(domain, placeHolder);
                            subscriber.onError(e);
                        }
                    }
                });

                subscriber.setCancellation(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        client.newCall(request).cancel();
                    }
                });
            }
        }, Emitter.BackpressureMode.NONE)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread());
    }

    String substringUntilDomain(String url) {
        return url.substring(0, url.indexOf("/", 8) + 1);
    }
}
