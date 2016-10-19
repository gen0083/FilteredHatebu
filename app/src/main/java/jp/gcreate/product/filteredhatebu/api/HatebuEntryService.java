package jp.gcreate.product.filteredhatebu.api;

import jp.gcreate.product.filteredhatebu.model.HatebuEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuEntryService {
    @GET("entry/json/")
    Observable<HatebuEntry> getEntry(@Query("url") String url);

    @GET("entry/jsonlite/")
    Call<HatebuEntry> getEntryNoRelated(@Query("url") String url);
}
