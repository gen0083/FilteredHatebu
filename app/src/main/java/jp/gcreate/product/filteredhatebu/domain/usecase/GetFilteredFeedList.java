package jp.gcreate.product.filteredhatebu.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet;
import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.di.qualifier.ObserveOn;
import jp.gcreate.product.filteredhatebu.di.qualifier.SubscribeOn;
import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
public class GetFilteredFeedList {
    private FeedsBurnerClienet feedsBurnerClienet;
    private HatenaClient.XmlService hatenaXmlService;
    private FilterRepository filterRepository;
    private Scheduler subscribeOn;
    private Scheduler observeOn;

    @Inject
    public GetFilteredFeedList(FeedsBurnerClienet feedsBurnerClienet,
                               HatenaClient.XmlService hatenaXmlService,
                               FilterRepository filterRepository,
                               @SubscribeOn Scheduler subscribeOn,
                               @ObserveOn Scheduler observeOn) {
        this.feedsBurnerClienet = feedsBurnerClienet;
        this.hatenaXmlService = hatenaXmlService;
        this.filterRepository = filterRepository;
        this.subscribeOn = subscribeOn;
        this.observeOn = observeOn;
    }

    public Observable<List<HatebuFeedItem>> getFilteredList(String key) {
        if (key.equals("")) {
            return getFilteredList(feedsBurnerClienet.getHotentryFeed());
        } else {
            return getFilteredList(hatenaXmlService.getCategoryFeed(key));
        }
    }

    private Observable<List<HatebuFeedItem>> getFilteredList(Observable<HatebuFeed> feedObservable) {
        return feedObservable
                .subscribeOn(subscribeOn)
                .flatMapIterable(new Func1<HatebuFeed, List<HatebuFeedItem>>() {
                    @Override
                    public List<HatebuFeedItem> call(HatebuFeed hatebuFeed) {
                        return hatebuFeed.getItemList();
                    }
                })
                .filter(new Func1<HatebuFeedItem, Boolean>() {
                    @Override
                    public Boolean call(HatebuFeedItem hatebuFeedItem) {
                        List<UriFilter> filters = filterRepository.getFilterAll().toBlocking().value();
                        for (UriFilter filter : filters) {
                            if (filter.isFilteredUrl(hatebuFeedItem.getLink())) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .toList()
                // 新着記事がない場合値が流れないので空のリストを流す
                .defaultIfEmpty(new ArrayList<HatebuFeedItem>())
                .observeOn(observeOn);
    }
}
