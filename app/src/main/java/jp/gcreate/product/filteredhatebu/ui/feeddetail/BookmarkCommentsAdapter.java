package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ItemEntryBookmarksBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;

/**
 * Copyright 2016 G-CREATE
 */

public class BookmarkCommentsAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemEntryBookmarksBinding>> {
    private final Context             context;
    private HatebuFeedDetailPresenter presenter;

    public BookmarkCommentsAdapter(Context context, HatebuFeedDetailPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public DataBindingViewHolder<ItemEntryBookmarksBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_entry_bookmarks, parent, false);
        return new DataBindingViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemEntryBookmarksBinding> holder, int position) {
        ItemEntryBookmarksBinding binding = holder.getBinding();
        HatebuBookmark bookmark = presenter.getItem(position);
        binding.setBookmark(bookmark);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
