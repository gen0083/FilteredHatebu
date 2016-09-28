package jp.gcreate.sample.daggersandbox.di;

import dagger.Component;
import jp.gcreate.sample.daggersandbox.MyApplication;
import jp.gcreate.sample.daggersandbox.di.Scope.AppScope;

/**
 * Copyright 2016 G-CREATE
 */

@AppScope
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MyApplication application);

    ActivityComponent plus(ActivityModule module);
}
