package jp.gcreate.product.filteredhatebu.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext

private const val ROOM_FILE = "hatebu.room"

@Module
class AppDataModule {
    
    @Provides @AppScope
    fun provideAppRoomDatabase(@ApplicationContext context: Context): AppRoomDatabase {
        return Room.databaseBuilder(context, AppRoomDatabase::class.java, ROOM_FILE)
            .build()
    }
}
