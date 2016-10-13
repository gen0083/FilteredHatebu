package jp.gcreate.sample.daggersandbox.data;

import java.util.List;
import java.util.NoSuchElementException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import jp.gcreate.sample.daggersandbox.di.Scope.AppScope;
import jp.gcreate.sample.daggersandbox.model.UriFilter;
import rx.Single;
import rx.SingleSubscriber;

/**
 * Copyright 2016 G-CREATE
 */
@AppScope
public class FilterDataSourceRealm implements FilterDataSource {
    private RealmConfiguration config;

    public FilterDataSourceRealm(RealmConfiguration config) {
        this.config = config;
    }

    @Override
    public void insertFilter(String insert) {
        Realm realm = Realm.getInstance(config);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(new UriFilter(insert));
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public Single<List<UriFilter>> getFilterAll() {
        return Single.create(new Single.OnSubscribe<List<UriFilter>>() {
            @Override
            public void call(SingleSubscriber<? super List<UriFilter>> singleSubscriber) {
                final Realm realm = Realm.getInstance(config);
                RealmResults<UriFilter> results = realm.where(UriFilter.class).findAll();
                if (results.size() == 0) {
                    singleSubscriber.onError(new NoSuchElementException("Filter has no data."));
                }
                singleSubscriber.onSuccess(results.subList(0, results.size()));
                realm.close();
            }
        });
    }

    @Override
    public Single<UriFilter> getFilter(final String filter) {
        return Single.create(new Single.OnSubscribe<UriFilter>() {
            @Override
            public void call(final SingleSubscriber<? super UriFilter> singleSubscriber) {
                final Realm realm = Realm.getInstance(config);
                RealmResults<UriFilter> results = realm.where(UriFilter.class)
                                                       .equalTo("filter", filter)
                                                       .findAll();
                if (results.size() == 1) {
                    singleSubscriber.onSuccess(results.get(0));
                } else {
                    singleSubscriber.onError(new NoSuchElementException("Filter '" + filter + "' is not exist."));
                }
                realm.close();
            }
        });
    }

    @Override
    public void updateFilter(String old, String update) {
        deleteFilter(old);
        insertFilter(update);
    }

    @Override
    public void deleteFilter(String delete) {
        Realm realm = Realm.getInstance(config);
        realm.beginTransaction();
        RealmResults<UriFilter> result = realm.where(UriFilter.class).equalTo("filter", delete).findAll();
        if (result.size() == 1) {
            result.get(0).deleteFromRealm();
        } else {
            for (int i = 0; i < result.size(); i++) {
                result.get(i).deleteFromRealm();
            }
        }
        realm.commitTransaction();
        realm.close();
    }
}
