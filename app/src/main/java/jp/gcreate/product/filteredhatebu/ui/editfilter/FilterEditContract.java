package jp.gcreate.product.filteredhatebu.ui.editfilter;

import jp.gcreate.product.filteredhatebu.model.UriFilter;

/**
 * Copyright 2016 G-CREATE
 */

public interface FilterEditContract {
    interface View {
        // RecyclerView
        void notifyDatasetChanged();
        void notifyItemChanged(int position);
        void notifyItemRemoved(int position);
        void notifyItemInserted(int position);
    }

    interface Presenter {
        void onAttach(View view);
        void onDetach();
        void delete(int position);
        void undoDelete();

        // RecyclerView
        int getItemCount();
        UriFilter getItem(int position);
        boolean isDeleted(int position);
    }
}
