package jp.gcreate.product.filteredhatebu.di

import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File

private const val OKHTTP_CACHE_DIR = "okhttp"
private const val OKHTTP_CACHE_SIZE = 4 * 1024 * 1024
val koinNetworkModule: Module = applicationContext {
    bean {
        val appContext = androidApplication()
        val cacheDir = File(appContext.cacheDir, OKHTTP_CACHE_DIR)
        val cache = Cache(cacheDir, OKHTTP_CACHE_SIZE.toLong())
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(get())
            .build()
    }
    bean {
        val retrofit = Retrofit.Builder()
            .baseUrl(FeedsBurnerClienet.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(FeedsBurnerClienet::class.java) as FeedsBurnerClienet
    }
    bean {
        val retrofit = Retrofit.Builder()
            .baseUrl(HatenaClient.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(HatenaClient.JsonService::class.java) as HatenaClient.JsonService
    }
    bean {
        val retrofit = Retrofit.Builder()
            .baseUrl(HatenaClient.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(HatenaClient.XmlService::class.java) as HatenaClient.XmlService
    }
}
