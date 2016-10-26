package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import com.github.gfx.android.orma.AccessThreadConstraint;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.data.FilterDataSource;
import jp.gcreate.product.filteredhatebu.data.FilterDataSourceOrma;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import jp.gcreate.product.filteredhatebu.model.OrmaDatabase;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class AppDataModule {
    public static final String ORMA_FILE = "hatebu.orma";

    @Provides
    @AppScope
    public FilterRepository provideFilterRepository(FilterDataSource dataSource) {
        return new FilterRepository(dataSource);
    }

    @Provides
    @AppScope
    public OrmaDatabase provideOrmaDatabase(@ApplicationContext Context context) {
        OrmaDatabase orma = OrmaDatabase.builder(context)
                                        .writeOnMainThread(AccessThreadConstraint.WARNING)
                                        .readOnMainThread(AccessThreadConstraint.WARNING)
                                        .name(ORMA_FILE)
                                        .build();
        return orma;
    }

    @Provides
    @AppScope
    public FilterDataSource provideFilterDataSource(OrmaDatabase orma) {
        return new FilterDataSourceOrma(orma);
    }
}
