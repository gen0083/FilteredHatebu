package jp.gcreate.product.filteredhatebu

import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import jp.gcreate.product.filteredhatebu.di.AppComponent
import jp.gcreate.product.filteredhatebu.di.AppModule
import jp.gcreate.product.filteredhatebu.di.DaggerAppComponent
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import jp.gcreate.product.filteredhatebu.util.CrashlyticsWrapper
import jp.gcreate.product.filteredhatebu.util.StethoWrapper
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
    @Inject lateinit var notificationUtil: NotificationUtil
    
    override fun onCreate() {
        super.onCreate()
        
        appComponent.inject(this)
        
        Timber.plant(tree)
        stetho.install()
        crashlyticsWrapper.init(this)
        AndroidThreeTen.init(this)
        Picasso.setSingletonInstance(picassoBuilder.build())
        scheduleCrawlFeedWork()
        notificationUtil.installChannel()
    }
    
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return appComponent
    }
    
    private fun scheduleCrawlFeedWork() {
        val constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.UNMETERED)
            setRequiresBatteryNotLow(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setRequiresDeviceIdle(true)
            }
        }
            .build()
        val request = PeriodicWorkRequestBuilder<CrawlFeedsWork>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag("repeat_crawling")
            .setInputData(mapOf(CrawlFeedsWork.KEY_TYPE to "period").toWorkData())
            .build()
        WorkManager.getInstance()
            ?.enqueueUniquePeriodicWork("repeat_crawling",
                                        ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
