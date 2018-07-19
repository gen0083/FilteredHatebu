package jp.gcreate.product.filteredhatebu.ui.favorite

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey

@Module
interface FavoriteDiModule {
    
    @FragmentScope @ContributesAndroidInjector
    fun buildFavoriteFragment(): FavoriteFragment
    
    @Binds @IntoMap @ViewModelKey(FavoriteViewModel::class)
    fun bindFavoriteViewModel(vm: FavoriteViewModel): ViewModel
}