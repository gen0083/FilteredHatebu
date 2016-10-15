package jp.gcreate.product.filteredhatebu.data;

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.model.OrmaDatabase;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;

/**
 * Copyright 2016 G-CREATE
 */

@AppScope
public class FilterDataSourceOrma implements FilterDataSource {
    private final OrmaDatabase orma;

    @Inject
    public FilterDataSourceOrma(OrmaDatabase orma) {
        this.orma = orma;
    }

    @Override
    public void insertFilter(String insert) {
        orma.insertIntoUriFilter(new UriFilter(insert));
    }

    @Override
    public Single<List<UriFilter>> getFilterAll() {
        return Single
                .create(new Single.OnSubscribe<List<UriFilter>>() {
                    @Override
                    public void call(SingleSubscriber<? super List<UriFilter>> singleSubscriber) {
                        List<UriFilter> list = orma.selectFromUriFilter()
                                                   .executeAsObservable().toList().toBlocking()
                                                   .single();
                        if (list.size() == 0) {
                            singleSubscriber.onError(new NoSuchElementException("No data."));
                        }
                        singleSubscriber.onSuccess(list);
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<UriFilter> getFilter(String filter) {
        return orma.selectFromUriFilter()
                   .filterEq(filter)
                   .executeAsObservable()
                   .toSingle();
    }

    @Override
    public void updateFilter(String old, String update) {
        orma.updateUriFilter()
            .filterEq(old)
            .filter(update)
            .execute();
    }

    @Override
    public void deleteFilter(String delete) {
        orma.deleteFromUriFilter()
            .filterEq(delete)
            .execute();
    }
}
