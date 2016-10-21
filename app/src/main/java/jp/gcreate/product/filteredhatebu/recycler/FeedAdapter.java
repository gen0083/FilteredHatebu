package jp.gcreate.product.filteredhatebu.recycler;

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
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class FeedAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemHatebuFeedBinding>>
        implements View.OnClickListener {
    private static final String FAVICON_URL = "https://favicon.hatena.ne.jp/?url=";
    private Context                      context;
    private List<HatebuFeedItem>         items;
    private RecyclerView                 recyclerView;
    private OnRecycelerItemClickListener listener;
    private FilterRepository             filterRepository;

    public FeedAdapter(Context context, FilterRepository filterRepository) {
        this(context, filterRepository, new ArrayList<HatebuFeedItem>());
    }

    public FeedAdapter(Context context, FilterRepository filterRepository,
                       List<HatebuFeedItem> items) {
        this.context = context;
        this.filterRepository = filterRepository;
        this.items = items;
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
        final HatebuFeedItem        item    = items.get(position);
        Timber.i("%s onBindViewHolder position:%d item:%s", this, position, item);
        filterRepository.getFilterAll()
                        .subscribe(new Action1<List<UriFilter>>() {
                            @Override
                            public void call(List<UriFilter> uriFilters) {
                                boolean b = false;
                                for (UriFilter f : uriFilters) {
                                    b = f.isFilteredUrl(item.getLink());
                                    if (b) break;
                                }
                                binding.setIsFiltered(b);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                binding.setIsFiltered(false);
                            }
                        });
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
        int count = items.size();
        return count;
    }

    public void setItemList(List<HatebuFeedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Timber.i("%s onAttachedRecyclerView:%s", this, recyclerView);
        this.recyclerView = recyclerView;
        this.context = recyclerView.getContext();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Timber.i("%s onDetachedFromRecyclerView:%s", this, recyclerView);
        this.recyclerView = null;
        this.context = null;
    }

    @Override
    public void onClick(View v) {
        Timber.i("%s onClick view:%s", this, v);
        int                  position = recyclerView.getChildAdapterPosition(v);
        final HatebuFeedItem item     = items.get(position);
        Timber.i("%s onClick position:%d", this, position);
        if (listener != null) {
            Timber.i("%s onClick: callback to %s", this, listener);
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
