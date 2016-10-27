package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.ItemHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class FeedAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemHatebuFeedBinding>>
        implements View.OnClickListener {
    private static final String FAVICON_URL = "https://favicon.hatena.ne.jp/?url=";
    private Context                      context;
    private List<HatebuFeedItem>         originList;
    private List<HatebuFeedItem>         shownList;
    private RecyclerView                 recyclerView;
    private OnRecycelerItemClickListener listener;
    private FilterRepository             filterRepository;
    private CompositeSubscription        compositeSubscription;
    private long                         previousModifiedTime;

    public FeedAdapter(Context context, FilterRepository filterRepository) {
        this(context, filterRepository, new ArrayList<HatebuFeedItem>());
    }

    public FeedAdapter(Context context, FilterRepository filterRepository,
                       List<HatebuFeedItem> items) {
        this.context = context;
        this.filterRepository = filterRepository;
        this.originList = items;
        updateShownList();
    }

    private void updateShownList() {
        shownList = new ArrayList<>();
        filterRepository.getFilterAll()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<UriFilter>>() {
                            @Override
                            public void call(List<UriFilter> uriFilters) {
                                for (final HatebuFeedItem item : originList) {
                                    boolean isFiltered = false;
                                    for (UriFilter f : uriFilters) {
                                        isFiltered = f.isFilteredUrl(item.getLink());
                                        if (isFiltered) break;
                                    }
                                    if (!isFiltered) {
                                        shownList.add(item);
                                    }
                                }
                                notifyDataSetChanged();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                shownList = originList;
                                notifyDataSetChanged();
                            }
                        });
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
        final HatebuFeedItem        item    = shownList.get(position);
        Timber.v("%s onBindViewHolder position:%d item:%s", this, position, item);
        binding.setItem(item);
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
        return shownList.size();
    }

    public void setItemList(List<HatebuFeedItem> items) {
        this.originList = items;
        updateShownList();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Timber.v("%s onAttachedRecyclerView:%s", this, recyclerView);
        this.recyclerView = recyclerView;
        this.context = recyclerView.getContext();
        compositeSubscription = new CompositeSubscription();
        Subscription s = filterRepository.listenModified()
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(new Action1<Long>() {
                                             @Override
                                             public void call(Long time) {
                                                 if (time > previousModifiedTime) {
                                                     // list updated
                                                     updateShownList();
                                                 }
                                                 previousModifiedTime = time;
                                             }
                                         });
        compositeSubscription.add(s);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Timber.v("%s onDetachedFromRecyclerView:%s", this, recyclerView);
        this.recyclerView = null;
        this.context = null;
        compositeSubscription.unsubscribe();
    }

    @Override
    public void onClick(View v) {
        Timber.v("%s onClick view:%s", this, v);
        int                  position = recyclerView.getChildAdapterPosition(v);
        final HatebuFeedItem item     = shownList.get(position);
        Timber.v("%s onClick position:%d", this, position);
        if (listener != null) {
            Timber.v("%s onClick: callback to %s", this, listener);
            listener.onClick(this, position, item);
        }
    }

    public interface OnRecycelerItemClickListener {
        void onClick(FeedAdapter adapter, int position, HatebuFeedItem item);
    }

    public void setOnRecyclerItemClickListener(OnRecycelerItemClickListener listener) {
        this.listener = listener;
    }
}
