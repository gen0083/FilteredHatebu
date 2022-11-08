package jp.gcreate.product.filteredhatebu.di

import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClient
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File

private const val OKHTTP_CACHE_DIR = "okhttp"
private const val OKHTTP_CACHE_SIZE = 4 * 1024 * 1024
val koinNetworkModule = module {
    single {
        val appContext = androidContext()
        val cacheDir = File(appContext.cacheDir, OKHTTP_CACHE_DIR)
        val cache = Cache(cacheDir, OKHTTP_CACHE_SIZE.toLong())
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(get())
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(FeedsBurnerClient.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(FeedsBurnerClient::class.java) as FeedsBurnerClient
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(HatenaClient.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(HatenaClient.JsonService::class.java) as HatenaClient.JsonService
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(HatenaClient.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(HatenaClient.XmlService::class.java) as HatenaClient.XmlService
    }
}
