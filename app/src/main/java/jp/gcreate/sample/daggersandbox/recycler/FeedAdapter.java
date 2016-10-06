package jp.gcreate.sample.daggersandbox.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.sample.daggersandbox.R;
import jp.gcreate.sample.daggersandbox.databinding.ItemHatebuFeedBinding;
import jp.gcreate.sample.daggersandbox.model.HatebuFeedItem;

/**
 * Copyright 2016 G-CREATE
 */

public class FeedAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemHatebuFeedBinding>>{
    private final Context context;
    private List<HatebuFeedItem> items;

    public FeedAdapter(Context context) {
        this(context, new ArrayList<HatebuFeedItem>());
    }

    public FeedAdapter(Context context, List<HatebuFeedItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public DataBindingViewHolder<ItemHatebuFeedBinding> onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hatebu_feed, parent, false);
        return new DataBindingViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemHatebuFeedBinding> holder,
                                 int position) {
        ItemHatebuFeedBinding binding = holder.getBinding();
        final HatebuFeedItem item = items.get(position);
        binding.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemList(List<HatebuFeedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
