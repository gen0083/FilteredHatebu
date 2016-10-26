package jp.gcreate.product.filteredhatebu.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomSheetBehavior;
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
import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.ActivityHatebuFeedDetailBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.fragment.SelectFilterDialogFragment;
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.model.HatebuEntry;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.recycler.BookmarksAdapter;
import jp.gcreate.product.filteredhatebu.util.BitmapUtil;
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
    private static final String TAG            = "FeedDetailActivity";
    private static final String EXTRA_ITEM_KEY = "feed_item_key";
    private static final int INTENT_SHARE_CODE = 1;
    private ActivityHatebuFeedDetailBinding   binding;
    private HatebuFeedItem                    item;
    private ActivityComponent                 component;
    private Subscription                      bookmarkSubscription;
    private BookmarksAdapter                  adapter;
    private BottomSheetBehavior<RecyclerView> bottomSheetBehavior;
    @Inject
    HatenaClient     service;
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
        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareUrl(item.getLink());
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
        bottomSheetBehavior = BottomSheetBehavior.from(r);
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

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void openCustomTab(String url) {
        final Bitmap shareIcon = BitmapUtil.getBitmap(this, R.drawable.ic_share);
        final Bitmap closeIcon = BitmapUtil.getBitmap(this, R.drawable.ic_arrow_back);
        final PendingIntent shareIntent = PendingIntent
                .getActivity(this, INTENT_SHARE_CODE, createShareUrlIntent(url), PendingIntent.FLAG_UPDATE_CURRENT);
        CustomTabsIntent i = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .setCloseButtonIcon(closeIcon)
                .setActionButton(shareIcon, getString(R.string.share_url), shareIntent)
                .addMenuItem(getString(R.string.share_url), shareIntent)
                .build();
        i.launchUrl(this, Uri.parse(url));
    }

    private void shareUrl(String url) {
        startActivity(createShareUrlIntent(url));
    }

    private Intent createShareUrlIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        return intent;
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
