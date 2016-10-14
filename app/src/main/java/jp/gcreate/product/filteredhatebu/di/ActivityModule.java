package jp.gcreate.product.filteredhatebu.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ActivityContext;

/**
 * Copyright 2016 G-CREATE
 */

@Module
public class ActivityModule {
    private Context activityContext;

    public ActivityModule(Context activityContext) {
        this.activityContext = activityContext;
    }

    public void updateContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    @Provides
    @ActivityContext
    public Context provideActivityContext() {
        return activityContext;
    }

    @Provides
    @ActivityScope
    public String provideStringTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Provides
    public long provideTime() {
        return System.currentTimeMillis();
    }
}
