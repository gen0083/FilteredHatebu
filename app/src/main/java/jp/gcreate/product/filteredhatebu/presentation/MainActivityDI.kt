package jp.gcreate.product.filteredhatebu.presentation

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import jp.gcreate.product.filteredhatebu.presentation.feeddetail.FeedDetailContributor
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListContributor
import jp.gcreate.product.filteredhatebu.presentation.filterlist.FilterListFragmentBuilder

@Module
interface MainActivityContributor {
    
    @ActivityScope
    @ContributesAndroidInjector(modules = [
        MainActivityModule::class,
        FeedDetailContributor::class,
        FeedListContributor::class,
        FilterListFragmentBuilder::class
    ])
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainActivityModule {
    
    @Binds fun bindActivity(activity: MainActivity): AppCompatActivity
}
