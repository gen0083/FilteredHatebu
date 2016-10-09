package jp.gcreate.sample.daggersandbox.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.sample.daggersandbox.R;
import jp.gcreate.sample.daggersandbox.databinding.ItemEntryBookmarksBinding;
import jp.gcreate.sample.daggersandbox.model.HatebuBookmark;

/**
 * Copyright 2016 G-CREATE
 */

public class BookmarksAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemEntryBookmarksBinding>> {
    private final Context             context;
    private List<HatebuBookmark> list;

    public BookmarksAdapter(Context context) {
        this(context, new ArrayList<HatebuBookmark>());
    }

    public BookmarksAdapter(Context context, List<HatebuBookmark> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public DataBindingViewHolder<ItemEntryBookmarksBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_entry_bookmarks, parent, false);
        return new DataBindingViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemEntryBookmarksBinding> holder, int position) {
        ItemEntryBookmarksBinding binding = holder.getBinding();
        HatebuBookmark bookmark = list.get(position);
        binding.setBookmark(bookmark);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<HatebuBookmark> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addItem(HatebuBookmark item) {
        list.add(item);
        notifyItemChanged(list.size());
    }
}
