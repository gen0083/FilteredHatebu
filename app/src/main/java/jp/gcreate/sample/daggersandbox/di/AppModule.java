package jp.gcreate.sample.daggersandbox.di;

import android.content.Context;
import android.hardware.SensorManager;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.sample.daggersandbox.DummyPojo;
import jp.gcreate.sample.daggersandbox.di.Scope.AppScope;
import jp.gcreate.sample.daggersandbox.di.qualifier.ApplicationContext;

/**
 * Copyright 2016 G-CREATE
 */

@Module
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
    @AppScope
    public DummyPojo provideDummyPojo(@ApplicationContext Context context) {
        return new DummyPojo(System.currentTimeMillis(), context.getPackageName() + System.currentTimeMillis());
    }
}
