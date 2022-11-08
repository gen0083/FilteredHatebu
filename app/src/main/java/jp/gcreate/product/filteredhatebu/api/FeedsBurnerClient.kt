package jp.gcreate.product.filteredhatebu.api

import jp.gcreate.product.filteredhatebu.api.response.HatebuFeed
import retrofit2.http.GET

/**
 * Copyright 2016 G-CREATE
 */
interface FeedsBurnerClient {
    @GET("hatena/b/hotentry")
    suspend fun getHotentryFeed(): HatebuFeed

    companion object {
        const val BASE_URL = "https://feeds.feedburner.com/"
    }
}