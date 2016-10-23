package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;
import android.hardware.SensorManager;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;

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

}
