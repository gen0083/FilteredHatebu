package jp.gcreate.product.filteredhatebu.ui.feedlist;

import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuFeedContract {
    interface Activity {
        interface View {
            void showLoading();
            void hideLoading();
            void notifyDataSetChanged();
        }

        interface ActivityPresenter {
            void onAttach(View view);
            void onDetach();
            // For fragments in ViewPager
            Fragment.FragmentPresenter getOrCreateFragmentPresenter(String key);
            // PagerAdapter
            android.support.v4.app.Fragment getItem(int position);
            String getPageTitle(int position);
            int getCount();
        }
    }

    interface Fragment {
        interface View {
            void showLoading();
            void hideLoading();
            void showNetworkError();
            void showNewContentsDoseNotExist();
            void launchFeedDetailActivity(HatebuFeedItem item);
            // RecyclerView
            void notifyDataSetChanged();
            void notifyItemChanged(int position);
        }

        interface FragmentPresenter {
            void onAttach(View view);
            void onDetach();
            void reloadList();
            // RecyclerAdapter
            HatebuFeedItem getItem(int position);
            int getItemCount();
            void onClick(int position);
        }
    }
}
