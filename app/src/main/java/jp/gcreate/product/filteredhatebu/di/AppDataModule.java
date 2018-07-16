package jp.gcreate.product.filteredhatebu.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppDataModule {
    public static final String ROOM_FILE = "hatebu.room";

    @Provides @AppScope
    public AppRoomDatabase provideAppRoomDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppRoomDatabase.class, ROOM_FILE)
                   // TODO: for developing
                   .fallbackToDestructiveMigration()
                   .build();
    }
}
