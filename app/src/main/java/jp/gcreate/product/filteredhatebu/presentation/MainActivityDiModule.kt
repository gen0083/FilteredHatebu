package jp.gcreate.product.filteredhatebu.presentation

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import jp.gcreate.product.filteredhatebu.presentation.feeddetail.FeedDetailDiModule
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListDiModule
import jp.gcreate.product.filteredhatebu.presentation.filterdetail.FilterDetailDiModule
import jp.gcreate.product.filteredhatebu.presentation.filterlist.FilterListDiModule

@Module
interface MainActivityDiModule {
    
    @ActivityScope
    @ContributesAndroidInjector(modules = [
        MainActivityModule::class,
        FeedDetailDiModule::class,
        FeedListDiModule::class,
        FilterListDiModule::class,
        FilterDetailDiModule::class
    ])
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainActivityModule {
    
    @Binds fun bindActivity(activity: MainActivity): AppCompatActivity
}
