package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.support.v7.widget.RecyclerView;

import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuFeedContract {
    interface Activity {
        interface View {
            void showLoading();
            void hideLoading();
            void showCategories();
        }

        interface ActivityPresenter {
            void loadCategories(View view);
            Fragment.Presenter getOrCreateFragmentPresenter(String key);
            // PagerAdapter
            Fragment getItem(int position);
            String getPageTitle(int position);
            int getCount();
        }
    }

    interface Fragment {
        interface View {
            void showList();
            void showLoading();
            void hideLoading();
        }

        interface Presenter {
            void onDisplay(View view);
            // RecyclerAdapter
            HatebuFeedItem getItem(int position);
            <T extends RecyclerView.ViewHolder> void onBindViewHolder(T holder, int position);
            int getItemCount();
            void onClick(int position);
        }
    }
}
