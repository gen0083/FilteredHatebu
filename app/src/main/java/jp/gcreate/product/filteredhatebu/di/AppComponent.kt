package jp.gcreate.product.filteredhatebu.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import jp.gcreate.product.filteredhatebu.CustomApplication
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork

/**
 * Copyright 2016 G-CREATE
 */
@AppScope
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    AppNetworkModule::class,
    AppDebugModule::class,
    AppDataModule::class
])
interface AppComponent : AndroidInjector<CustomApplication>{

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(@ApplicationContext application: CustomApplication): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
    
    override fun inject(application: CustomApplication)
    fun inject(work: CrawlFeedsWork)
}
