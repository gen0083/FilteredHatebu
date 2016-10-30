package jp.gcreate.product.filteredhatebu.api;

import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Copyright 2016 G-CREATE
 */

public interface FeedsBurnerClienet {
    public static final String BASE_URL = "http://feeds.feedburner.com/";

    @GET("hatena/b/hotentry")
    Observable<HatebuFeed> getHotentryFeed();
}
