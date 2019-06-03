package jp.gcreate.product.filteredhatebu.di

import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import jp.gcreate.product.filteredhatebu.util.CrashlyticsWrapper
import jp.gcreate.product.filteredhatebu.util.ReleaseLogTree
import jp.gcreate.product.filteredhatebu.util.StethoWrapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import timber.log.Timber

val koinDebugModule = module {
    single { StethoWrapper() }
    single { ReleaseLogTree() as Timber.Tree }
    single {
        HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { Timber.tag("OkHttp").v(it) }
        )
            .setLevel(HttpLoggingInterceptor.Level.HEADERS) as Interceptor
    }
    single {
        Picasso.Builder(androidContext())
            .downloader(OkHttp3Downloader(get<OkHttpClient>()))
            .loggingEnabled(true)
            .indicatorsEnabled(true)
    }
    single { CrashlyticsWrapper() }
}
