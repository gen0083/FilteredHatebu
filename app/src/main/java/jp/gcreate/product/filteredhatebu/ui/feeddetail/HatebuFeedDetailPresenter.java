package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark;
import jp.gcreate.product.filteredhatebu.model.HatebuEntry;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
public class HatebuFeedDetailPresenter implements HatebuFeedDetailContract.Presenter {
    private HatebuFeedDetailContract.View view;
    private FilterRepository              filterRepository;
    private HatenaClient.JsonService      jsonService;
    private Subscription                  loadingSubscription;
    private HatebuFeedItem                item;
    private boolean              isFirstTime = true;
    private List<HatebuBookmark> comments    = new ArrayList<>();
    private String previousAdded;

    @Inject
    public HatebuFeedDetailPresenter(FilterRepository filterRepository,
                                     HatenaClient.JsonService jsonService) {
        this.filterRepository = filterRepository;
        this.jsonService = jsonService;
    }

    @Override
    public void onAttach(HatebuFeedDetailContract.View view, HatebuFeedItem item) {
        this.view = view;
        if (isFirstTime) {
            isFirstTime = false;
            this.item = item;
            fetchBookmarkComments();
        } else {
            if (this.item != null && item.getLink().equals(this.item.getLink())) {
                // same url
                if (comments.size() == 0) {
                    showNoComments();
                } else {
                    view.notifyDataSetChanged();
                }
            } else {
                // refresh comments
                this.item = item;
                comments = new ArrayList<>();
                fetchBookmarkComments();
            }
        }
        if (isLoading()) {
            showLoading();
        } else {
            hideLoading();
        }
    }

    @Override
    public void onDetach() {
        this.view = null;
    }

    private void fetchBookmarkComments() {
        if (isLoading()) {
            Timber.d("still loading");
            return;
        }
        showLoading();
        loadingSubscription = jsonService
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
                .defaultIfEmpty(HatebuBookmark.EMPTY)
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        hideLoading();
                    }
                })
                .subscribe(new Action1<HatebuBookmark>() {
                    @Override
                    public void call(HatebuBookmark hatebuBookmark) {
                        if (hatebuBookmark.equals(HatebuBookmark.EMPTY)) {
                            showNoComments();
                        } else {
                            comments.add(hatebuBookmark);
                            notifyItemInserted(comments.size());
                        }
                    }
                });
    }

    private boolean isLoading() {
        return loadingSubscription != null && !loadingSubscription.isUnsubscribed();
    }

    @Override
    public void addFilter(String filter) {
        filterRepository.insertFilter(filter);
        previousAdded = filter;
    }

    @Override
    public void cancelAddedFilter() {
        if (!previousAdded.equals("")) {
            filterRepository.deleteFilter(previousAdded);
            previousAdded = "";
        }
    }

    @Override
    public HatebuBookmark getItem(int position) {
        return comments.get(position);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private void showLoading() {
        if (view != null) {
            view.showLoading();
        }
    }

    private void hideLoading() {
        if (view != null) {
            view.hideLoading();
        }
    }

    private void showNoComments() {
        if (view != null) {
            view.showNoComments();
        }
    }

    private void notifyItemInserted(int position) {
        if (view != null) {
            view.notifyItemInserted(position);
        }
    }
}
