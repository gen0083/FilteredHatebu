package jp.gcreate.sample.daggersandbox.di;

import android.content.Context;
import android.hardware.SensorManager;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.sample.daggersandbox.DummyPojo;
import jp.gcreate.sample.daggersandbox.di.Scope.AppScope;

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
    public Context provicdeContext() {
        return context;
    }

    @Provides
    @AppScope
    public SensorManager provideSensorManager() {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return manager;
    }

    @Provides
    public String provideString() {
        return "from AppModule";
    }

    @Provides
    @AppScope
    public DummyPojo provideDummyPojo(Context context) {
        return new DummyPojo(System.currentTimeMillis(), context.getPackageName() + System.currentTimeMillis());
    }
}
