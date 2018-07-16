package jp.gcreate.product.filteredhatebu.ui.filterlist

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey

@Module
interface FilterListDiModule {
    
    @FragmentScope @ContributesAndroidInjector
    fun buildFilterListFragment(): FilterListFragment
    
    @Binds @IntoMap @ViewModelKey(
        FilterListViewModel::class)
    fun bindFilterListViewModel(viewModel: FilterListViewModel): ViewModel
}
