package jp.gcreate.product.filteredhatebu.di;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmConfiguration;
import jp.gcreate.product.filteredhatebu.data.FilterDataSource;
import jp.gcreate.product.filteredhatebu.data.FilterDataSourceRealm;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppDataModule {
    private static final String REALM_FILE = "hatebu.realm";

    @Provides
    @AppScope
    public RealmConfiguration provideRealmConfigration() {
        RealmConfiguration c = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .name(REALM_FILE)
                .build();
        return c;
    }

    @Provides
    @AppScope
    public FilterDataSource provideFilterDataSource(RealmConfiguration config) {
        return new FilterDataSourceRealm(config);
    }
}
