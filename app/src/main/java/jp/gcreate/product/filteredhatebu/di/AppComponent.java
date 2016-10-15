package jp.gcreate.product.filteredhatebu.di;

import dagger.Component;
import jp.gcreate.product.filteredhatebu.MyApplication;
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;

/**
 * Copyright 2016 G-CREATE
 */

@AppScope
@Component(modules = {AppModule.class, AppNetworkModule.class, AppDebugModule.class,
                      AppDataModule.class})
public interface AppComponent {

    void inject(MyApplication application);

    @AppScope
    ActivityComponent plus(ActivityModule module);
}
