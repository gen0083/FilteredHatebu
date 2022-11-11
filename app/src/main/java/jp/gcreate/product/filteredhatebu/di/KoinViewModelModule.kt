package jp.gcreate.product.filteredhatebu.di

import jp.gcreate.product.filteredhatebu.ui.favorite.FavoriteViewModel
import jp.gcreate.product.filteredhatebu.ui.feeddetail.FeedDetailViewModel
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListViewModel
import jp.gcreate.product.filteredhatebu.ui.filterdetail.FilterDetailViewModel
import jp.gcreate.product.filteredhatebu.ui.filterlist.FilterListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val koinViewModelModule = module {
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::FeedDetailViewModel)
    viewModelOf(::FeedListViewModel)
    viewModelOf(::FilterDetailViewModel)
    viewModelOf(::FilterListViewModel)
}
