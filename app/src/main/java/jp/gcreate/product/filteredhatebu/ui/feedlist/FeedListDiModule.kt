package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey

@Module
interface FeedListDiModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [FeedListFragmentModule::class])
    fun contributeFeedListFragment(): FeedListFragment
    
    @Binds @IntoMap @ViewModelKey(FeedListViewModel::class)
    fun bindFeedListViewModel(viewModel: FeedListViewModel): ViewModel
}

@Module
interface FeedListFragmentModule {
    @Binds fun bindFragment(fragment: FeedListFragment): Fragment
}
