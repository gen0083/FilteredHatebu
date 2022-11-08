package jp.gcreate.product.filteredhatebu

import jp.gcreate.product.filteredhatebu.api.HatenaClient.JsonService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Copyright 2016 G-CREATE
 */
class RetrofitTest {
    private lateinit var retrofit: Retrofit
    private lateinit var service: JsonService
    private val BASE_URL = "http://b.hatena.ne.jp/"

    @Before
    fun setUp() {
        val logger = HttpLoggingInterceptor { message -> println(message) }
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(logger)
            .addInterceptor(logger)
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
        service = retrofit.create(JsonService::class.java)
    }

    @Test
    fun test_call() = runBlocking {
        val entry = service
            .getEntry("http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getinfo")
        println("gotten entry: $entry")
        println("bookmarks:" + entry.bookmarks.size)
        for (bookmark in entry.bookmarks) {
            println(bookmark)
        }
    }
}