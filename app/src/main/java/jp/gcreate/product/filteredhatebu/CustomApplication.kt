package jp.gcreate.product.filteredhatebu

import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import jp.gcreate.product.filteredhatebu.di.AppComponent
import jp.gcreate.product.filteredhatebu.di.AppModule
import jp.gcreate.product.filteredhatebu.di.DaggerAppComponent
import jp.gcreate.product.filteredhatebu.util.CrashlyticsWrapper
import jp.gcreate.product.filteredhatebu.util.StethoWrapper
import timber.log.Timber
import javax.inject.Inject

/**
 * Copyright 2016 G-CREATE
 */
class CustomApplication : DaggerApplication() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .appModule(AppModule(this))
            .build()
    }
    @Inject
    lateinit var stetho: StethoWrapper
    @Inject
    lateinit var tree: Timber.Tree
    @Inject
    lateinit var picassoBuilder: Picasso.Builder
    @Inject
    lateinit var crashlyticsWrapper: CrashlyticsWrapper

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)

        Timber.plant(tree)
        stetho.install()
        crashlyticsWrapper.init(this)
        AndroidThreeTen.init(this)
        Picasso.setSingletonInstance(picassoBuilder.build())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return appComponent
    }
}
