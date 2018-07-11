package jp.gcreate.product.filteredhatebu.di;

import dagger.Subcomponent;
import jp.gcreate.product.filteredhatebu.presentation.archive.ArchivedFeedFragment;
import jp.gcreate.product.filteredhatebu.presentation.feeddetail.FeedDetailFragment;
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListFragment;
import jp.gcreate.product.filteredhatebu.ui.editfilter.FilterEditActivity;
import jp.gcreate.product.filteredhatebu.ui.feedlist.HatebuFeedActivity;
import jp.gcreate.product.filteredhatebu.ui.feeddetail.HatebuFeedDetailActivity;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.ui.feedlist.HatebuFeedFragment;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(HatebuFeedActivity activity);
    void inject(HatebuFeedDetailActivity activity);
    void inject(FilterEditActivity activity);

    void inject(HatebuFeedFragment fragment);

    void inject(FeedListFragment fragment);
    void inject(FeedDetailFragment fragment);
    void inject(ArchivedFeedFragment fragment);
}
