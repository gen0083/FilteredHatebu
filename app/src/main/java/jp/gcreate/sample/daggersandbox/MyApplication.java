package jp.gcreate.sample.daggersandbox;

import android.app.Activity;
import android.app.Application;

import jp.gcreate.sample.daggersandbox.di.AppComponent;
import jp.gcreate.sample.daggersandbox.di.AppModule;
import jp.gcreate.sample.daggersandbox.di.DaggerAppComponent;

/**
 * Copyright 2016 G-CREATE
 */

public class MyApplication extends Application {
    private AppComponent appComponent;

    public static AppComponent getAppComponent(Activity activity) {
        MyApplication application = (MyApplication) activity.getApplication();
        return application.appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                                         .appModule(new AppModule(this))
                                         .build();
    }
}
