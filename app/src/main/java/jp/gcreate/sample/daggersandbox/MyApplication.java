package jp.gcreate.sample.daggersandbox;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import jp.gcreate.sample.daggersandbox.di.ActivityComponent;
import jp.gcreate.sample.daggersandbox.di.ActivityModule;
import jp.gcreate.sample.daggersandbox.di.AppComponent;
import jp.gcreate.sample.daggersandbox.di.AppModule;
import jp.gcreate.sample.daggersandbox.di.DaggerAppComponent;

/**
 * Copyright 2016 G-CREATE
 */

public class MyApplication extends Application {
    private AppComponent appComponent;
    @Inject @Named("application")
    Context      context;

    public static AppComponent getAppComponent(Activity activity) {
        MyApplication application = (MyApplication) activity.getApplication();
        return application.appComponent;
    }

    public static ActivityComponent getActivityComponent(Activity activity) {
        AppComponent component = getAppComponent(activity);
        return component.plus(new ActivityModule(activity));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                                         .appModule(new AppModule(this))
                                         .build();
        appComponent.inject(this);

        Log.d("test", "application:" + context.toString());
    }
}
