package jp.gcreate.sample.daggersandbox.di;

import javax.inject.Singleton;

import dagger.Component;
import jp.gcreate.sample.daggersandbox.MainActivity;
import jp.gcreate.sample.daggersandbox.MyApplication;

/**
 * Copyright 2016 G-CREATE
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MyApplication application);
    void inject(MainActivity activity);
}
