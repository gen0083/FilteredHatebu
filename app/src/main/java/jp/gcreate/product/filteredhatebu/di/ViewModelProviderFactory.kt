package jp.gcreate.product.filteredhatebu.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

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