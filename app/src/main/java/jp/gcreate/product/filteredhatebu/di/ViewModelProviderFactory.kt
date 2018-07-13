package jp.gcreate.product.filteredhatebu.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class ViewModelProviderFactory @Inject constructor(
    private val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
): ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Timber.d("create ViewModel with $modelClass")
        return viewModels[modelClass]?.get() as T
               ?: throw IllegalArgumentException("unknown model class: $modelClass")
    }
}
