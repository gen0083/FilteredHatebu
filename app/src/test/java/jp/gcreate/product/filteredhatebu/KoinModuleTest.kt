package jp.gcreate.product.filteredhatebu

import android.app.Application
import io.mockk.mockk
import jp.gcreate.product.filteredhatebu.di.koinAppModule
import jp.gcreate.product.filteredhatebu.di.koinDataModule
import jp.gcreate.product.filteredhatebu.di.koinDebugModule
import jp.gcreate.product.filteredhatebu.di.koinNetworkModule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class KoinModuleTest : KoinTest {
    @Test fun koin_module_test() {
        val testModule = module {
            single { mockk<Application>() }
        }
    
        startKoin {
            modules(
                listOf(testModule, koinAppModule, koinDataModule, koinNetworkModule,
                    koinDebugModule)
            )
        }
    }
}
