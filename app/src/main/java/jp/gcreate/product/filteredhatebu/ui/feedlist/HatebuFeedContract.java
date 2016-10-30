package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.support.v4.app.Fragment;

import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuFeedContract {
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

        // PagerAdapter
        Fragment getItem(int position);

        String getPageTitle(int position);

        int getCount();
    }

    interface FragmentView {
        void showLoading();

        void hideLoading();

        void showNetworkError();

        void showNewContentsDoseNotExist();

        void launchFeedDetailActivity(HatebuFeedItem item);

        // RecyclerView
        void notifyDataSetChanged();

        void notifyItemChanged(int position);
    }

    interface ChildPresenter {
        void onAttach(FragmentView view);

        void onDetach();

        void reloadList();

        // RecyclerAdapter
        HatebuFeedItem getItem(int position);

        int getItemCount();

        void onClick(int position);
    }
}
