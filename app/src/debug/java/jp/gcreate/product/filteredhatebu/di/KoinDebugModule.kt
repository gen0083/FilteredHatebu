package jp.gcreate.product.filteredhatebu.di

import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import jp.gcreate.product.filteredhatebu.util.CrashlyticsWrapper
import jp.gcreate.product.filteredhatebu.util.StethoWrapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import timber.log.Timber

val koinDebugModule: Module = applicationContext {
    bean { StethoWrapper(get("applicationContext")) }
    bean { Timber.DebugTree() as Timber.Tree }
    bean {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Timber.tag("OkHttp").v(it) })
            .setLevel(HttpLoggingInterceptor.Level.HEADERS) as Interceptor
    }
    bean {
        Picasso.Builder(get("applicationContext"))
            .downloader(OkHttp3Downloader(get<OkHttpClient>()))
            .loggingEnabled(true)
            .indicatorsEnabled(true)
    }
    bean { CrashlyticsWrapper() }
}
