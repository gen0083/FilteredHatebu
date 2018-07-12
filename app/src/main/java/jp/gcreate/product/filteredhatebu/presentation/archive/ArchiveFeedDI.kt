package jp.gcreate.product.filteredhatebu.presentation.archive

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ViewModelKey

@Module
interface ArchiveFeedContributor {
    @FragmentScope
    @ContributesAndroidInjector(modules = [ArchiveFeedModule::class])
    fun contributeArchiveFeedFragment(): ArchivedFeedFragment
}

@Module
interface ArchiveFeedModule {
    @Binds fun bindFragment(fragment: ArchivedFeedFragment): Fragment
    
    @Binds @IntoMap @ViewModelKey(ArchivedFeedViewModel::class)
    fun bindArchiveFeedViewModel(viewModel: ArchivedFeedViewModel): ViewModel
}
