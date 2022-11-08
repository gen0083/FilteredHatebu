package jp.gcreate.product.filteredhatebu.api

import jp.gcreate.product.filteredhatebu.api.response.HatebuEntry
import jp.gcreate.product.filteredhatebu.api.response.HatebuFeed
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Copyright 2016 G-CREATE
 */
interface HatenaClient {
    interface JsonService {
        @GET("entry/json/")
        suspend fun getEntry(@Query("url") url: String): HatebuEntry

        @GET("entry/jsonlite/")
        suspend fun getEntryNoRelated(@Query("url") url: String): HatebuEntry

        @GET("entry/jsonlite/")
        suspend fun getHatebuEntry(@Query("url") url: String?): HatebuEntry
    }

    interface XmlService {
        @GET("hotentry/{category}.rss")
        suspend fun getCategoryFeed(@Path("category") category: String): HatebuFeed
    }

    companion object {
        const val BASE_URL = "https://b.hatena.ne.jp/"
    }
}