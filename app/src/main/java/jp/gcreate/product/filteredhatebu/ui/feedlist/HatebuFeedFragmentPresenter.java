package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.domain.usecase.GetFilteredFeedList;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

class HatebuFeedFragmentPresenter implements HatebuFeedContract.ChildPresenter {
    private final String                          categoryKey;
    private       GetFilteredFeedList             getFilteredFeedList;
    private       HatebuFeedContract.FragmentView view;
    private       long                            previousModifiedTime;
    private       Subscription                    loadingSubscription;
    private       FaviconUtil                     faviconUtil;
    private List<HatebuFeedItem> filteredList = new ArrayList<>();
    private boolean              isFirstTime  = true;

    HatebuFeedFragmentPresenter(String key,
                                final GetFilteredFeedList getFilteredFeedList,
                                final FilterRepository filterRepository,
                                final FaviconUtil faviconUtil) {
        this.categoryKey = key;
        this.getFilteredFeedList = getFilteredFeedList;
        this.faviconUtil = faviconUtil;
        filterRepository.listenModified()
                        .subscribeOn(Schedulers.io())
                        .filter(new Func1<Long, Boolean>() {
                            @Override
                            public Boolean call(Long updated) {
                                return updated > previousModifiedTime;
                            }
                        })
                        .doOnNext(new Action1<Long>() {
                            @Override
                            public void call(Long updated) {
                                previousModifiedTime = updated;
                            }
                        })
                        .concatMap(new Func1<Long, Observable<List<HatebuFeedItem>>>() {
                            @Override
                            public Observable<List<HatebuFeedItem>> call(Long aLong) {
                                return getFilteredFeedList.getFilteredList(categoryKey);
                            }
                        })
                        .filter(new Func1<List<HatebuFeedItem>, Boolean>() {
                            @Override
                            public Boolean call(List<HatebuFeedItem> hatebuFeedItems) {
                                return checkBothListsAreEqual(hatebuFeedItems, filteredList);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<HatebuFeedItem>>() {
                            @Override
                            public void call(List<HatebuFeedItem> hatebuFeedItems) {
                                filteredList = hatebuFeedItems;
                                notifyFilterUpdated();
                            }
                        });
    }

    @Override
    public void onAttach(HatebuFeedContract.FragmentView view) {
        this.view = view;
        if (isFirstTime) {
            reloadList();
            isFirstTime = false;
        }
        if (isLoading()) {
            showLoading();
        } else {
            hideLoading();
        }
        // これを呼ばないとviewがattachされていないときに発生した変更が反映されない
        // (フィルタを追加したのにフィルタされないとか)
        view.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        this.view = null;
    }

    @Override
    public void reloadList() {
        if (isLoading()) {
            Timber.d("%s[cat:%s] is now loading, skip reloading", this, categoryKey);
            return;
        }
        showLoading();
        loadingSubscription = getFilteredFeedList
                .getFilteredList(categoryKey)
                .subscribe(new Action1<List<HatebuFeedItem>>() {
                    @Override
                    public void call(List<HatebuFeedItem> filtered) {
                        hideLoading();
                        if (checkBothListsAreEqual(filtered, filteredList)) {
                            Timber.d("Feeds are not updated.");
                            notifyNewContentsDoseNotExist();
                        } else {
                            Timber.d("Got some new feeds.");
                            filteredList = filtered;
                            notifyNewContentsFetched();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e("error %s", throwable.getMessage());
                        throwable.printStackTrace();
                        hideLoading();
                        showNetworkError();
                    }
                });
    }

    private boolean checkBothListsAreEqual(List<HatebuFeedItem> a, List<HatebuFeedItem> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!(a.get(i).equals(b.get(i)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Observable<Drawable> fetchFavicon(String url) {
        return faviconUtil.fetchFavicon(url);
    }

    private boolean isLoading() {
        return loadingSubscription != null && !loadingSubscription.isUnsubscribed();
    }

    @Override
    public HatebuFeedItem getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public void onClick(int position) {
        if (view != null) {
            view.launchFeedDetailActivity(filteredList.get(position));
        }
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

    private void notifyFilterUpdated() {
        if (view != null) {
            view.notifyFilterUpdated();
        }
    }

    private void notifyNewContentsFetched() {
        if (view != null) {
            view.notifyNewContentsFetched();
        }
    }

    private void notifyNewContentsDoseNotExist() {
        if (view != null) {
            view.notifyNewContentsDoseNotExist();
        }
    }

    private void showNetworkError() {
        if (view != null) {
            view.showNetworkError();
        }
    }

}
