package jp.gcreate.product.filteredhatebu.di

import android.arch.persistence.room.Room
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

private const val ROOM_FILE = "hatebu.room"
val koinDataModule: Module = applicationContext {
    bean {
        Room.databaseBuilder(androidApplication(), AppRoomDatabase::class.java, ROOM_FILE)
            .build()
    }
}
