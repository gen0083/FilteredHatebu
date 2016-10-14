package jp.gcreate.product.filteredhatebu.di;

import dagger.Subcomponent;
import jp.gcreate.product.filteredhatebu.LoginActivity;
import jp.gcreate.product.filteredhatebu.MainActivity;
import jp.gcreate.product.filteredhatebu.activity.HatebuFeedDetailActivity;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.fragment.HatebuFeedFragment;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(HatebuFeedDetailActivity activity);

    void inject(HatebuFeedFragment fragment);
}
