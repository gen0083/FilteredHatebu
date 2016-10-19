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
    private static final String FAVION_URL = "https://favicon.hatena.ne.jp/?url=";
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
        Timber.i("%s constructor called.", this);
    }

    @Override
    public DataBindingViewHolder<ItemHatebuFeedBinding> onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        Timber.i("%s onCreateViewHolder", this);
        View view = LayoutInflater.from(context).inflate(R.layout.item_hatebu_feed, parent, false);
        view.setOnClickListener(this);
        return new DataBindingViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemHatebuFeedBinding> holder,
                                 int position) {
        Timber.i("%s onBindViewHolder", this);
        final ItemHatebuFeedBinding binding = holder.getBinding();
        final HatebuFeedItem        item    = items.get(position);
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
               .load(FAVION_URL + item.getLink())
               .placeholder(R.drawable.favicon_placeholder)
               .error(R.drawable.favicon_placeholder)
               .fit()
               .into(binding.favicon);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        int count = items.size();
        Timber.i("%s getItemCount:%d", this, count);
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
