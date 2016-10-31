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
                                final FilterRepository filterRepository) {
        this.categoryKey = key;
        this.filterRepository = filterRepository;
        feedObservable = (key.equals("")) ? feedsBurnerClienet.getHotentryFeed() :
                         hatenaService.getCategoryFeed(categoryKey);
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
                        .concatMap(new Func1<Long, Observable<List<UriFilter>>>() {
                            @Override
                            public Observable<List<UriFilter>> call(Long aLong) {
                                return filterRepository.getFilterAll().toObservable();
                            }
                        })
                        .map(new Func1<List<UriFilter>, List<HatebuFeedItem>>() {
                            @Override
                            public List<HatebuFeedItem> call(List<UriFilter> uriFilters) {
                                return filterOriginList(originList, uriFilters);
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
        loadingSubscription = feedObservable
                .subscribeOn(Schedulers.io())
                .map(new Func1<HatebuFeed, List<HatebuFeedItem>>() {
                    @Override
                    public List<HatebuFeedItem> call(HatebuFeed hatebuFeed) {
                        return hatebuFeed.getItemList();
                    }
                })
                .filter(new Func1<List<HatebuFeedItem>, Boolean>() {
                    @Override
                    public Boolean call(List<HatebuFeedItem> newList) {
                        // 新しい記事の内容を全て含む＝新しい記事なしを意味する
                        // 新しい内容がある場合のみ値を流す
                        return !originList.containsAll(newList);
                    }
                })
                .map(new Func1<List<HatebuFeedItem>, List<HatebuFeedItem>>() {
                    @Override
                    public List<HatebuFeedItem> call(List<HatebuFeedItem> hatebuFeedItems) {
                        originList = hatebuFeedItems;
                        // 別スレッドで処理しているのでblockingで問題ない
                        List<UriFilter> filters = filterRepository.getFilterAll().toBlocking().value();
                        // フィルタ処理した後のリストを流す
                        return filterOriginList(hatebuFeedItems, filters);
                    }
                })
                // 新着記事がない場合値が流れないので空のリストを流す
                .defaultIfEmpty(new ArrayList<HatebuFeedItem>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<HatebuFeedItem>>() {
                    @Override
                    public void call(List<HatebuFeedItem> filtered) {
                        hideLoading();
                        // defaultIfEmpty Listのサイズが0なら新しいFeedがなかったことを意味する
                        // (.filterの部分で値が止まってしまった状態)
                        if (filtered.size() == 0) {
                            Timber.d("there are not new one.");
                            notifyNewContentsDoseNotExist();
                        } else {
                            Timber.d("got new feeds");
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

    private boolean isLoading() {
        return loadingSubscription != null && !loadingSubscription.isUnsubscribed();
    }

    private List<HatebuFeedItem> filterOriginList(final List<HatebuFeedItem> origin,
                                                  final List<UriFilter> filters) {
        if (filters.size() == 0) return new ArrayList<>(origin);
        List<HatebuFeedItem> filteredList = new ArrayList<>();
        for (HatebuFeedItem item : origin) {
            boolean isFiltered = false;
            for (UriFilter f : filters) {
                isFiltered = f.isFilteredUrl(item.getLink());
                if (isFiltered) break;
            }
            if (!isFiltered) filteredList.add(item);
        }
        return filteredList;
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
