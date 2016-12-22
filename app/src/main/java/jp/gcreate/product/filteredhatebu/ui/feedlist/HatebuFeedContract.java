package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import rx.Observable;

/**
 * Copyright 2016 G-CREATE
 */

interface HatebuFeedContract {
    interface ActivityView {
        void showLoading();

        void hideLoading();

        void notifyDataSetChanged();
    }

    interface ParentPresenter {
        void onAttach(ActivityView activity);

        void onDetach();

        // For fragments in ViewPager
        ChildPresenter getOrCreateFragmentPresenter(String key);

        void reloadFeeds(int position);

        // PagerAdapter
        Fragment getItem(int position);

        String getPageTitle(int position);

        int getCount();
    }

    interface FragmentView {
        void showLoading();

        void hideLoading();

        void notifyFilterUpdated();

        void notifyNewContentsFetched();

        void notifyNewContentsDoseNotExist();

        void showNetworkError();

        void launchFeedDetailActivity(HatebuFeedItem item);

        // RecyclerView
        void notifyDataSetChanged();
    }

    interface ChildPresenter {
        void onAttach(FragmentView view);

        void onDetach();

        void reloadList();

        Observable<Drawable> fetchFavicon(String url);

        // RecyclerAdapter
        HatebuFeedItem getItem(int position);

        int getItemCount();

        void onClick(int position);
    }
}
