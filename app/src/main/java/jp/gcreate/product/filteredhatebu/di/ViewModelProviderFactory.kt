package jp.gcreate.product.filteredhatebu.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

// ViewModelKeyをSubcomponentで定義している関係上、このクラスにスコープを付けることができない
// （FragmentScopeにすることは可能）
// 本当ならAppScopeでシングルトンで保持してもらって問題ないのだが、
// そのためにはViewModelKeyを例えばAppModuleですべて定義する必要がある
// しかしそのためには、Fragmentを追加するたびにAppModuleにも手を加えなければいけなくなる
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
