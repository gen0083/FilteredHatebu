package jp.gcreate.sample.daggersandbox.api;

import jp.gcreate.sample.daggersandbox.model.HatebuEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuService {
    @GET("entry/json/")
    Observable<HatebuEntry> getEntry(@Query("url") String url);

    @GET("entry/jsonlite/")
    Call<HatebuEntry> getEntryLite(@Query("url") String url);
}