package jp.gcreate.product.filteredhatebu.ui.feedlist;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet;
import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Single;
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
    private       FilterRepository                filterRepository;
    private       HatebuFeedContract.FragmentView view;
    private       Observable<HatebuFeed>          feedObservable;
    private       long                            previousModifiedTime;
    private       Subscription                    loadingSubscription;
    private List<HatebuFeedItem> originList   = new ArrayList<>();
    private List<HatebuFeedItem> filteredList = new ArrayList<>();
    private boolean              isFirstTime  = true;

    HatebuFeedFragmentPresenter(String key,
                                FeedsBurnerClienet feedsBurnerClienet,
                                HatenaClient.XmlService hatenaService,
                                FilterRepository filterRepository) {
        this.categoryKey = key;
        this.filterRepository = filterRepository;
        feedObservable = (key.equals("")) ? feedsBurnerClienet.getHotentryFeed() :
                         hatenaService.getCategoryFeed(categoryKey);
        filterRepository.listenModified()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long time) {
                                if (time > previousModifiedTime) {
                                    // list updated
                                    updateFilteredList();
                                }
                                previousModifiedTime = time;
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
        loadingSubscription = feedObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HatebuFeed>() {
                    @Override
                    public void call(HatebuFeed hatebuFeed) {
                        // HatebuFeedはレスポンス本体で、実際に必要な各記事のリストはgetItemListで取得する
                        Timber.d("feed got: %d", hatebuFeed.getItemList().size());
                        List<HatebuFeedItem> newList = hatebuFeed.getItemList();
                        if (originList.containsAll(newList)) {
                            Timber.d("there are not new one.");
                            if (view != null) {
                                view.showNewContentsDoseNotExist();
                            }
                        } else {
                            Timber.d("got new feeds");
                            originList = newList;
                            updateFilteredList();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e("error %s", throwable.getMessage());
                        throwable.printStackTrace();
                        if (view != null) {
                            view.showNetworkError();
                        }
                    }
                });
    }

    private boolean isLoading() {
        return loadingSubscription != null && !loadingSubscription.isUnsubscribed();
    }

    private void updateFilteredList() {
        filterRepository.getFilterAll()
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<List<UriFilter>, List<HatebuFeedItem>>() {
                            @Override
                            public List<HatebuFeedItem> call(List<UriFilter> uriFilters) {
                                Timber.d("updateFilteredList map to List<HatebuFeedItem> on %s ",
                                         Thread.currentThread());
                                filteredList = new ArrayList<>();
                                for (final HatebuFeedItem item : originList) {
                                    boolean isFiltered = false;
                                    for (UriFilter f : uriFilters) {
                                        isFiltered = f.isFilteredUrl(item.getLink());
                                        if (isFiltered) break;
                                    }
                                    if (!isFiltered) {
                                        filteredList.add(item);
                                    }
                                }
                                return filteredList;
                            }
                        })
                        .onErrorResumeNext(
                                new Func1<Throwable, Single<? extends List<HatebuFeedItem>>>() {
                                    @Override
                                    public Single<? extends List<HatebuFeedItem>> call(
                                            Throwable throwable) {
                                        Timber.d("updateFilteredList onError at getFilterAll");
                                        filteredList.addAll(originList);
                                        return Single.just(filteredList);
                                    }
                                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<HatebuFeedItem>>() {
                            @Override
                            public void call(List<HatebuFeedItem> filteredList) {
                                Timber.d("updateFilteredList done on %s", Thread.currentThread());
                                if (view != null) {
                                    view.notifyDataSetChanged();
                                }
                            }
                        });
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
}
