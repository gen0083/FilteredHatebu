package jp.gcreate.product.filteredhatebu.di

import androidx.room.Room
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

private const val ROOM_FILE = "hatebu.room"
val koinDataModule: Module = module {
    single {
        Room.databaseBuilder(androidApplication(), AppRoomDatabase::class.java, ROOM_FILE)
            .build()
    }
}
