package jp.gcreate.product.filteredhatebu.di

import android.content.Context
import android.text.Spannable
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import jp.gcreate.product.filteredhatebu.ui.common.UrlSpanFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

val koinAppModule: Module = applicationContext {
    factory("applicationContext") { androidApplication() as Context }
    factory("subscribeOn") { Schedulers.io() as Scheduler }
    factory("observeOn") { AndroidSchedulers.mainThread() as Scheduler }
    // ui.common
    bean { UrlSpanFactory(get()) as Spannable.Factory }
    bean { CustomTabHelper(get("applicationContext")) }
    bean { FaviconUtil(get(), get("applicationContext")) }
    bean { NotificationUtil(get("applicationContext")) }
}
