package jp.gcreate.sample.daggersandbox.recycler;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Copyright 2016 G-CREATE
 */

public class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private T binding;

    public DataBindingViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public T getBinding() {
        return binding;
    }
}
