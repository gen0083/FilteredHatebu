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
import org.koin.core.qualifier.named
import org.koin.dsl.module
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

val koinAppModule = module {
    factory(named("subscribeOn")) { Schedulers.io() as Scheduler }
    factory(named("observeOn")) { AndroidSchedulers.mainThread() as Scheduler }
    // domain
    single { BookmarkCommentsService(get()) }
    single { FilterService(get()) }
    single { ArchiveFeedService(get()) }
    // ui.common
    single { UrlSpanFactory(get()) }
    single { CustomTabHelper(androidApplication()) }
    single { FaviconUtil(get(), androidApplication()) }
    single { NotificationUtil(androidApplication()) }
    // ui
    factory { FeedCommentsAdapter(get()) }
    factory { FeedListAdapter(get()) }
    factory { PagingFeedListAdapter(get()) }
    factory { FilterListAdapter() }
}
