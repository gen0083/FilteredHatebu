package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;

/**
 * Copyright 2016 G-CREATE
 */

public interface HatebuFeedDetailContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showNoComments();
        // for RecyclerView
        void notifyDataSetChanged();
        void notifyItemChanged(int position);
        void notifyItemInserted(int position);
    }

    interface Presenter {
        void onAttach(View view, HatebuFeedItem item);
        void onDetach();
        void addFilter(String filter);
        void cancelAddedFilter();
        // for RecyclerView
        HatebuBookmark getItem(int position);
        int getItemCount();
    }
}
