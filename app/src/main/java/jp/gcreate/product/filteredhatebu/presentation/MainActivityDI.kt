package jp.gcreate.product.filteredhatebu.presentation

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope

@Module
interface MainActivityContributor {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun contributeMainActivity(): MainActivity
}

@Module
interface MainActivityModule {
    @Binds fun bindActivity(activity: MainActivity): AppCompatActivity
}
