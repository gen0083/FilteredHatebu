package jp.gcreate.product.filteredhatebu

import android.app.Application
import android.support.test.InstrumentationRegistry
import jp.gcreate.product.filteredhatebu.di.koinAppModule
import jp.gcreate.product.filteredhatebu.di.koinDataModule
import jp.gcreate.product.filteredhatebu.di.koinDebugModule
import jp.gcreate.product.filteredhatebu.di.koinNetworkModule
import jp.gcreate.product.filteredhatebu.di.koinViewModelModule
import org.junit.Test
import org.koin.android.ext.koin.with
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.dryRun

class KoinModuleTest : KoinTest {
    @Test fun koin_module_test() {
        startKoin(listOf(
            koinAppModule,
            koinDataModule,
            koinDebugModule,
            koinNetworkModule,
            koinViewModelModule
        )) with InstrumentationRegistry.getTargetContext().applicationContext as Application
        dryRun()
        closeKoin()
    }
}
