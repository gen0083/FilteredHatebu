package jp.gcreate.product.filteredhatebu.api;

import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuFeedService {
    @GET("hotentry/{category}.rss")
    Observable<HatebuFeed> getCategoryFeed(@Path("category") String category);
}
