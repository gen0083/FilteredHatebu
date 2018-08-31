package jp.gcreate.product.filteredhatebu.di

import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.BookmarkCommentsService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import jp.gcreate.product.filteredhatebu.ui.common.UrlSpanFactory
import jp.gcreate.product.filteredhatebu.ui.feeddetail.FeedCommentsAdapter
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListAdapter
import jp.gcreate.product.filteredhatebu.ui.feedlist.PagingFeedListAdapter
import jp.gcreate.product.filteredhatebu.ui.filterlist.FilterListAdapter
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

val koinAppModule: Module = applicationContext {
    factory("subscribeOn") { Schedulers.io() as Scheduler }
    factory("observeOn") { AndroidSchedulers.mainThread() as Scheduler }
    // domain
    bean { BookmarkCommentsService(get()) }
    bean { FilterService(get()) }
    bean { ArchiveFeedService(get()) }
    // ui.common
    bean { UrlSpanFactory(get()) }
    bean { CustomTabHelper(androidApplication()) }
    bean { FaviconUtil(get(), androidApplication()) }
    bean { NotificationUtil(androidApplication()) }
    // ui
    factory { FeedCommentsAdapter(get()) }
    factory { FeedListAdapter(get()) }
    factory { PagingFeedListAdapter(get()) }
    factory { FilterListAdapter() }
}
