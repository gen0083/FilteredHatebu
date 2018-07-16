package jp.gcreate.product.filteredhatebu.presentation.filterdetail

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey

@Module
interface FilterDetailDiModule {
    
    @FragmentScope @ContributesAndroidInjector
    fun buildFilterDetailFragment(): FilterDetailFragment
    
    @Binds @IntoMap @ViewModelKey(FilterDetailViewModel::class)
    fun bindFilterDetailViewModel(vm: FilterDetailViewModel): ViewModel
}
