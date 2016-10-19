package jp.gcreate.product.filteredhatebu.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.CustomApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.api.HatebuService;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.ActivityHatebuFeedDetailBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.fragment.SelectFilterDialogFragment;
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.model.HatebuEntry;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.recycler.BookmarksAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedDetailActivity extends AppCompatActivity
        implements SelectFilterDialogFragment.Callback {
    private static final String EXTRA_ITEM_KEY = "feed_item_key";
    private ActivityHatebuFeedDetailBinding binding;
    private HatebuFeedItem                  item;
    private ActivityComponent               component;
    private Subscription                    bookmarkSubscription;
    private BookmarksAdapter                adapter;
    @Inject
    HatebuService    service;
    @Inject
    FilterRepository filterRepository;

    public static Intent createIntent(Context context, HatebuFeedItem item) {
        Intent i = new Intent(context, HatebuFeedDetailActivity.class);
        i.putExtra(EXTRA_ITEM_KEY, item);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hatebu_feed_detail);

        component = CustomApplication.getActivityComponent(this);
        component.inject(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ITEM_KEY)) {
            item = intent.getParcelableExtra(EXTRA_ITEM_KEY);
        } else {
            throw new RuntimeException("This activity must set HatebuFeedItem.");
        }

        setupRecyclerView();

        binding.readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomTab(item.getLink());
            }
        });
        binding.addFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new BookmarksAdapter(this);
        RecyclerView r = binding.recyclerView;
        r.setLayoutManager(new LinearLayoutManager(this));
        r.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setItem(item);
        bookmarkSubscription = service
                .getEntry(item.getLink())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(new Func1<HatebuEntry, Iterable<HatebuBookmark>>() {
                    @Override
                    public Iterable<HatebuBookmark> call(HatebuEntry hatebuEntry) {
                        return hatebuEntry.getBookmarks();
                    }
                }).filter(new Func1<HatebuBookmark, Boolean>() {
                    @Override
                    public Boolean call(HatebuBookmark hatebuBookmark) {
                        return !TextUtils.isEmpty(hatebuBookmark.getComment());
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HatebuBookmark>() {
                    @Override
                    public void call(HatebuBookmark hatebuBookmark) {
                        adapter.addItem(hatebuBookmark);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bookmarkSubscription != null && !bookmarkSubscription.isUnsubscribed()) {
            bookmarkSubscription.unsubscribe();
        }
    }

    private void openCustomTab(String url) {
        CustomTabsIntent i = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .build();
        i.launchUrl(this, Uri.parse(url));
    }

    private void openFilterDialog() {
        SelectFilterDialogFragment f = SelectFilterDialogFragment.newInstance(item.getLink());
        f.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onCanceled() {
        Timber.d("%s onCanceled from AlertDialog.", this);
    }

    @Override
    public void onSelected(final String selected) {
        Timber.d("%s onSelected from AlertDialog selected:%s", this, selected);
        filterRepository.insertFilter(selected);
        Snackbar.make(binding.title, getString(R.string.add_filter_done, selected),
                      Snackbar.LENGTH_SHORT)
                .setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterRepository.deleteFilter(selected);
                    }
                })
                .show();
    }
}
