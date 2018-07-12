package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;
import android.hardware.SensorManager;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import jp.gcreate.product.filteredhatebu.di.qualifier.ObserveOn;
import jp.gcreate.product.filteredhatebu.di.qualifier.SubscribeOn;
import jp.gcreate.product.filteredhatebu.presentation.MainActivityContributor;
import jp.gcreate.product.filteredhatebu.presentation.archive.ArchiveFeedContributor;
import jp.gcreate.product.filteredhatebu.presentation.feeddetail.FeedDetailContributor;
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListContributor;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright 2016 G-CREATE
 */

@Module(includes = {MainActivityContributor.class, ArchiveFeedContributor.class,
                    FeedDetailContributor.class, FeedListContributor.class})
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @AppScope
    @ApplicationContext
    public Context provideContext() {
        return context;
    }

    @Provides
    @AppScope
    public SensorManager provideSensorManager() {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return manager;
    }

    @Provides
    @SubscribeOn
    public Scheduler provideSubscribeOnScheduler() {
        return Schedulers.io();
    }

    @Provides
    @ObserveOn
    public Scheduler provideObserveOnScheduler() {
        return AndroidSchedulers.mainThread();
    }

}
