package jp.gcreate.product.filteredhatebu

import jp.gcreate.product.filteredhatebu.api.HatenaClient.JsonService
import jp.gcreate.product.filteredhatebu.api.HatenaClient.XmlService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

/**
 * Copyright 2016 G-CREATE
 */
class HatebuFeedTest {
    private lateinit var service: XmlService
    private lateinit var jsonService: JsonService

    @Before
    fun setUp() {
        val logger = HttpLoggingInterceptor { message -> println(message) }
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(logger)
            .addInterceptor(logger)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
        service = retrofit.create(XmlService::class.java)
        val forJson = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
        jsonService = forJson.create(JsonService::class.java)
    }

    @Test
    fun `ITカテゴリホッテントリ取得`() = runBlocking {
        val hatebuFeed = service.getCategoryFeed("it")
        for (item in hatebuFeed.itemList) {
            println(item.toString())
        }
    }

    @Test
    fun `コメント取得`() = runBlocking {
        val hatebuEntry = jsonService
            .getEntryNoRelated("http://serihiro.hatenablog.com/entry/2016/12/22/000000")
        println(hatebuEntry)
    }

    companion object {
        private const val BASE_URL = "http://b.hatena.ne.jp/"
    }
}