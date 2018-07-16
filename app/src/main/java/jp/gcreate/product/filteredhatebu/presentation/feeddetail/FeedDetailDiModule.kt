package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey

@Module
interface FeedDetailDiModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [FeedDetailModule::class])
    fun contributeFeedDetailFragment(): FeedDetailFragment
    
    @FragmentScope @ContributesAndroidInjector
    fun contributePickFilterDialogFragment(): PickFilterDialogFragment
    
    @Binds @IntoMap @ViewModelKey(FeedDetailViewModel::class)
    fun bindFeedDetailViewModel(viewModel: FeedDetailViewModel): ViewModel
}

@Module
interface FeedDetailModule {
    @Binds fun bindFragment(fragment: FeedDetailFragment): Fragment
}
