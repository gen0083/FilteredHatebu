package jp.gcreate.product.filteredhatebu.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey
import jp.gcreate.product.filteredhatebu.presentation.feeddetail.FeedDetailViewModel
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListViewModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@Module
abstract class ViewModelModule {
    @Binds @IntoMap @ViewModelKey(FeedListViewModel::class)
    abstract fun bindFeedListViewModel(viewModel: FeedListViewModel): ViewModel
    
    @Binds @IntoMap @ViewModelKey(FeedDetailViewModel::class)
    abstract fun bindFeedDetailViewModel(viewModel: FeedDetailViewModel): ViewModel
}

@AppScope
class ViewModelProviderFactory @Inject constructor(
    private val viewModels: Map<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Timber.d("create ViewModel with $modelClass")
        return viewModels[modelClass]?.get() as T
               ?: throw IllegalArgumentException("unknown model class: $modelClass")
    }
}