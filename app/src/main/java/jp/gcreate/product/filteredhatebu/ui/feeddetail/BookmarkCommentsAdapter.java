package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ItemEntryBookmarksBinding;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ActivityContext;
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;
import jp.gcreate.product.filteredhatebu.ui.common.UrlSpanFactory;

/**
 * Copyright 2016 G-CREATE
 */
@ActivityScope
public class BookmarkCommentsAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemEntryBookmarksBinding>> {
    private final Context             context;
    private HatebuFeedDetailPresenter presenter;
    private UrlSpanFactory factory;

    @Inject
    public BookmarkCommentsAdapter(@ActivityContext Context context,
                                   HatebuFeedDetailPresenter presenter,
                                   UrlSpanFactory factory) {
        this.context = context;
        this.presenter = presenter;
        this.factory = factory;
    }

    @Override
    public DataBindingViewHolder<ItemEntryBookmarksBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_entry_bookmarks, parent, false);
        TextView textView = view.findViewById(R.id.comment);
        textView.setSpannableFactory(factory);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
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
