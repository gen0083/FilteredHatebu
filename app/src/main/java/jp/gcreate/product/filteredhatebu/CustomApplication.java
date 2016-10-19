package jp.gcreate.product.filteredhatebu;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.HashMap;

import javax.inject.Inject;

import io.realm.Realm;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.di.ActivityModule;
import jp.gcreate.product.filteredhatebu.di.AppComponent;
import jp.gcreate.product.filteredhatebu.di.AppModule;
import jp.gcreate.product.filteredhatebu.di.DaggerAppComponent;
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext;
import jp.gcreate.product.filteredhatebu.util.StethoWrapper;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class CustomApplication extends Application {
    private AppComponent appComponent;
    private HashMap<String, ActivityComponent> activityComponentSet = new HashMap<>();
    private HashMap<String, ActivityModule> activityModuleMap = new HashMap<>();
    @Inject @ApplicationContext
    Context      context;
    @Inject
    StethoWrapper stetho;
    @Inject
    Timber.Tree tree;

    public static AppComponent getAppComponent(Context context) {
        CustomApplication application = (CustomApplication) context.getApplicationContext();
        return application.appComponent;
    }

    public static ActivityComponent getActivityComponent(Activity activity) {
        String key = activity.getClass().getSimpleName();
        Timber.d("getActivityComponent: activity name:%s", key);
        CustomApplication application = (CustomApplication) activity.getApplication();
        if (application.hasComponent(key)) {
            application.getActivityModule(key).updateContext(activity);
            return application.getActivityComponent(key);
        } else {
            AppComponent appComponent = getAppComponent(activity);
            ActivityModule module = new ActivityModule(activity);
            ActivityComponent activityComponent = appComponent.plus(module);
            application.setActivityComponent(key, activityComponent);
            application.setActivityModule(key, module);
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

        Timber.plant(tree);
        stetho.install();
        Realm.init(this);

        Timber.d("application:%s", context.toString());
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

    public void setActivityModule(String key, ActivityModule module) {
        activityModuleMap.put(key, module);
    }

    public ActivityModule getActivityModule(String key) {
        return activityModuleMap.get(key);
    }

    public boolean hasModule(String key) {
        return activityModuleMap.containsKey(key);
    }
}
