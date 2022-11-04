package jp.gcreate.product.filteredhatebu.di

import androidx.room.Room
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.MIGRATION_6_7
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val ROOM_FILE = "hatebu.room"
val koinDataModule: Module = module {
    single {
        Room.databaseBuilder(androidContext(), AppRoomDatabase::class.java, ROOM_FILE)
            .addMigrations(MIGRATION_6_7)
            .build()
    }
}
