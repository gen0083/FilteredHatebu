package jp.gcreate.product.filteredhatebu

import android.app.Application
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.toWorkData
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.picasso.Picasso
import jp.gcreate.product.filteredhatebu.di.koinAppModule
import jp.gcreate.product.filteredhatebu.di.koinDataModule
import jp.gcreate.product.filteredhatebu.di.koinDebugModule
import jp.gcreate.product.filteredhatebu.di.koinNetworkModule
import jp.gcreate.product.filteredhatebu.di.koinViewModelModule
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import jp.gcreate.product.filteredhatebu.util.CrashlyticsWrapper
import jp.gcreate.product.filteredhatebu.util.StethoWrapper
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Copyright 2016 G-CREATE
 */
class CustomApplication : Application() {
    
    val stetho: StethoWrapper by inject()
    val tree: Timber.Tree by inject()
    val picassoBuilder: Picasso.Builder by inject()
    val crashlyticsWrapper: CrashlyticsWrapper by inject()
    val notificationUtil: NotificationUtil by inject()
    
    override fun onCreate() {
        super.onCreate()
    
        startKoin(this, listOf(
            koinAppModule,
            koinDataModule,
            koinDebugModule,
            koinNetworkModule,
            koinViewModelModule
        ))
        
        Timber.plant(tree)
        stetho.install()
        crashlyticsWrapper.init(this)
        AndroidThreeTen.init(this)
        Picasso.setSingletonInstance(picassoBuilder.build())
        scheduleCrawlFeedWork()
        notificationUtil.installChannel()
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
        val request = PeriodicWorkRequestBuilder<CrawlFeedsWork>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag("repeat_crawling")
            .setInputData(mapOf(CrawlFeedsWork.KEY_TYPE to "period").toWorkData())
            .build()
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("repeat_crawling", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
