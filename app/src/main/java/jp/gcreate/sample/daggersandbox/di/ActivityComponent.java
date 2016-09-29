package jp.gcreate.sample.daggersandbox.di;

import dagger.Subcomponent;
import jp.gcreate.sample.daggersandbox.LoginActivity;
import jp.gcreate.sample.daggersandbox.MainActivity;
import jp.gcreate.sample.daggersandbox.di.Scope.ActivityScope;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
}
