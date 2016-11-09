package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ItemHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class FeedAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemHatebuFeedBinding>>
        implements View.OnClickListener {
    public static final String FAVICON_URL = "https://favicon.hatena.ne.jp/?url=";
    private Context                           context;
    private RecyclerView                      recyclerView;
    private HatebuFeedContract.ChildPresenter presenter;
    private SparseArray<Subscription>         subscriptions = new SparseArray<>();

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
        Timber.v("onBindViewHolder position:%d item:%s", position, item);
        Timber.v("binding address:%d favicon address:%d position:%d", binding.hashCode(),
                 binding.favicon.hashCode(), position);
        binding.setItem(item);
        Subscription s = presenter.fetchFavicon(item.getLink())
                     .subscribe(new Action1<Drawable>() {
                         @Override
                         public void call(Drawable drawable) {
                             binding.favicon.setImageDrawable(drawable);
                         }
                     }, new Action1<Throwable>() {
                         @Override
                         public void call(Throwable throwable) {
                             Timber.e(throwable);
                             binding.favicon.setImageResource(R.drawable.favicon_placeholder);
                         }
                     });
        subscriptions.put(binding.hashCode(), s);
        binding.executePendingBindings();
    }

    @Override
    public void onViewRecycled(DataBindingViewHolder<ItemHatebuFeedBinding> holder) {
        super.onViewRecycled(holder);
        ItemHatebuFeedBinding binding = holder.getBinding();
        Timber.v("onViewRecycled binding:%d", binding.hashCode());
        unsubscribeByHashCode(binding.hashCode());
    }

    private void unsubscribeByHashCode(int hashCode) {
        if (subscriptions.get(hashCode) != null &&
            !subscriptions.get(hashCode).isUnsubscribed()) {
            subscriptions.get(hashCode).unsubscribe();
        }
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
        int position = recyclerView.getChildAdapterPosition(v);
        presenter.onClick(position);
    }
}
