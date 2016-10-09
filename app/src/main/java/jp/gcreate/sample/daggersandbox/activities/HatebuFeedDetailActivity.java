package jp.gcreate.sample.daggersandbox.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import javax.inject.Inject;

import jp.gcreate.sample.daggersandbox.MyApplication;
import jp.gcreate.sample.daggersandbox.R;
import jp.gcreate.sample.daggersandbox.api.HatebuService;
import jp.gcreate.sample.daggersandbox.databinding.ActivityHatebuFeedDetailBinding;
import jp.gcreate.sample.daggersandbox.di.ActivityComponent;
import jp.gcreate.sample.daggersandbox.model.HatebuBookmark;
import jp.gcreate.sample.daggersandbox.model.HatebuEntry;
import jp.gcreate.sample.daggersandbox.model.HatebuFeedItem;
import jp.gcreate.sample.daggersandbox.recycler.BookmarksAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedDetailActivity extends AppCompatActivity {
    private static final String EXTRA_ITEM_KEY = "feed_item_key";
    private ActivityHatebuFeedDetailBinding binding;
    private HatebuFeedItem                  item;
    private ActivityComponent               component;
    private Subscription                    bookmarkSubscription;
    private BookmarksAdapter adapter;
    @Inject
    HatebuService service;

    public static Intent createIntent(Context context, HatebuFeedItem item) {
        Intent i = new Intent(context, HatebuFeedDetailActivity.class);
        i.putExtra(EXTRA_ITEM_KEY, item);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hatebu_feed_detail);

        component = MyApplication.getActivityComponent(this);
        component.inject(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ITEM_KEY)) {
            item = intent.getParcelableExtra(EXTRA_ITEM_KEY);
        } else {
            throw new RuntimeException("This activity must set HatebuFeedItem.");
        }

        setupRecyclerView();
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
}
