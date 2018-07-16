package jp.gcreate.product.filteredhatebu.ui

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import jp.gcreate.product.filteredhatebu.ui.feeddetail.FeedDetailDiModule
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListDiModule
import jp.gcreate.product.filteredhatebu.ui.filterdetail.FilterDetailDiModule
import jp.gcreate.product.filteredhatebu.ui.filterlist.FilterListDiModule

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
