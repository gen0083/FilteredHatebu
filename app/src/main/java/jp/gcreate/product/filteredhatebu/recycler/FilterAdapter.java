package jp.gcreate.product.filteredhatebu.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ItemFilterBinding;
import jp.gcreate.product.filteredhatebu.model.UriFilter;

/**
 * Copyright 2016 G-CREATE
 */

public class FilterAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemFilterBinding>> {
    private Context         context;
    private List<UriFilter> list;

    public FilterAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public DataBindingViewHolder<ItemFilterBinding> onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false);
        return new DataBindingViewHolder<>(v);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemFilterBinding> holder, int position) {
        ItemFilterBinding binding = holder.getBinding();
        UriFilter item = list.get(position);
        binding.setItem(item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<UriFilter> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
