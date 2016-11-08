package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ItemHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class FeedAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemHatebuFeedBinding>>
        implements View.OnClickListener {
    private static final String FAVICON_URL = "https://favicon.hatena.ne.jp/?url=";
    private Context                                    context;
    private RecyclerView                               recyclerView;
    private HatebuFeedContract.ChildPresenter presenter;

    public FeedAdapter(Context context, HatebuFeedContract.ChildPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public DataBindingViewHolder<ItemHatebuFeedBinding> onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hatebu_feed, parent, false);
        view.setOnClickListener(this);
        return new DataBindingViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemHatebuFeedBinding> holder,
                                 final int position) {
        final ItemHatebuFeedBinding binding = holder.getBinding();
        final HatebuFeedItem        item    = presenter.getItem(position);
        Timber.v("%s onBindViewHolder position:%d item:%s", this, position, item);
        binding.setItem(item);
        Picasso.with(context)
                .cancelRequest(binding.favicon);
        Picasso.with(context)
               .load(FAVICON_URL + item.getLink())
               .placeholder(R.drawable.favicon_placeholder)
               .error(R.drawable.favicon_placeholder)
               .fit()
               .into(binding.favicon);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Timber.v("%s onAttachedRecyclerView:%s", this, recyclerView);
        this.recyclerView = recyclerView;
        this.context = recyclerView.getContext();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Timber.v("%s onDetachedFromRecyclerView:%s", this, recyclerView);
        this.recyclerView = null;
        this.context = null;
    }

    @Override
    public void onClick(View v) {
        Timber.v("%s onClick view:%s", this, v);
        int                  position = recyclerView.getChildAdapterPosition(v);
        presenter.onClick(position);
    }
}
