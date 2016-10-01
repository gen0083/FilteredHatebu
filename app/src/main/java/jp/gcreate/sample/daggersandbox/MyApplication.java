package jp.gcreate.sample.daggersandbox;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;

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
    private HashMap<String, ActivityComponent> activityComponentSet = new HashMap<>();
    @Inject @Named("application")
    Context      context;

    public static AppComponent getAppComponent(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.appComponent;
    }

    public static ActivityComponent getActivityComponent(Activity activity) {
        String key = activity.getClass().getSimpleName();
        Log.d("test", "getActivityComponent: activity name:" + key);
        MyApplication application = (MyApplication) activity.getApplication();
        if (application.hasComponent(key)) {
            return application.getActivityComponent(key);
        } else {
            AppComponent appComponent = getAppComponent(activity);
            ActivityComponent activityComponent = appComponent.plus(new ActivityModule());
            application.setActivityComponent(key, activityComponent);
            return activityComponent;
        }
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

    public boolean hasComponent(String key) {
        return activityComponentSet.containsKey(key);
    }

    public ActivityComponent getActivityComponent(String key) {
        return activityComponentSet.get(key);
    }

    public void setActivityComponent(String key, ActivityComponent component) {
        activityComponentSet.put(key, component);
    }
}
