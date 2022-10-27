package jp.gcreate.product.filteredhatebu

import android.app.Application
import android.os.Build
import androidx.work.*
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.picasso.Picasso
import jp.gcreate.product.filteredhatebu.di.*
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.domain.DeleteFeedsWork
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Copyright 2016 G-CREATE
 */
class CustomApplication : Application() {
    
    val tree: Timber.Tree by inject()
    val picassoBuilder: Picasso.Builder by inject()
    val notificationUtil: NotificationUtil by inject()
    
    override fun onCreate() {
        super.onCreate()
    
        startKoin {
            androidContext(this@CustomApplication)
            modules(
                listOf(
                    koinAppModule,
                    koinDataModule,
                    koinDebugModule,
                    koinNetworkModule,
                    koinViewModelModule
                )
            )
        }
        
        Timber.plant(tree)
        AndroidThreeTen.init(this)
        Picasso.setSingletonInstance(picassoBuilder.build())
        scheduleCrawlFeedWork()
        DeleteFeedsWork.schedule()
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
            .setInputData(workDataOf(CrawlFeedsWork.KEY_TYPE to "period"))
            .build()
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("repeat_crawling", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
