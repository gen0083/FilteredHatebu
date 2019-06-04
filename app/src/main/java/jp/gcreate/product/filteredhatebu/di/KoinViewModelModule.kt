package jp.gcreate.product.filteredhatebu.di

import jp.gcreate.product.filteredhatebu.ui.favorite.FavoriteViewModel
import jp.gcreate.product.filteredhatebu.ui.feeddetail.FeedDetailViewModel
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListViewModel
import jp.gcreate.product.filteredhatebu.ui.filterdetail.FilterDetailViewModel
import jp.gcreate.product.filteredhatebu.ui.filterlist.FilterListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinViewModelModule = module {
    viewModel { FavoriteViewModel(get()) }
    viewModel { FeedDetailViewModel(get(), get(), get(), get()) }
    viewModel { FeedListViewModel(get(), get(), get()) }
    viewModel { FilterDetailViewModel(get(), get()) }
    viewModel { FilterListViewModel(get(), get(), get()) }
}
