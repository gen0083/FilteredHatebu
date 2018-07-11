package jp.gcreate.product.filteredhatebu.api;

import jp.gcreate.product.filteredhatebu.model.HatebuEntry;
import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatenaClient {
    public static final String BASE_URL     = "https://b.hatena.ne.jp/";

    interface JsonService {
        @GET("entry/json/")
        Observable<HatebuEntry> getEntry(@Query("url") String url);

        @GET("entry/jsonlite/")
        Observable<HatebuEntry> getEntryNoRelated(@Query("url") String url);

        @GET("entry/jsonlite/")
        Call<HatebuEntry> getHatebuEntry(@Query("url") String url);
    }

    interface XmlService {
        @GET("hotentry/{category}.rss")
        Observable<HatebuFeed> getCategoryFeed(@Path("category") String category);
    }
}
