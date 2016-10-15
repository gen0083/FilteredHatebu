package jp.gcreate.product.filteredhatebu.data;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.di.Scope.AppScope;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */
@AppScope
public class FilterRepository implements FilterDataSource {
    private List<UriFilter> cachedList = new ArrayList<>();
    private boolean         isDirty    = true;
    private FilterDataSource localDataSource;

    @Inject
    public FilterRepository(FilterDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public void insertFilter(String insert) {
        localDataSource.insertFilter(insert);
        cachedList.add(new UriFilter(insert));
    }

    @Override
    public Single<List<UriFilter>> getFilterAll() {
        if (isDirty) {
            return localDataSource.getFilterAll()
                                  .doOnSuccess(new Action1<List<UriFilter>>() {
                                      @Override
                                      public void call(List<UriFilter> uriFilters) {
                                          Timber.d("%s getFilterAll size: %d", this,
                                                   uriFilters.size());
                                          cachedList = uriFilters;
                                          isDirty = false;
                                      }
                                  });
        } else {
            return Single.just(cachedList);
        }
    }

    @Override
    public Single<UriFilter> getFilter(final String filter) {
        if (isDirty) {
            return localDataSource.getFilter(filter);
        } else {
            return Observable.from(cachedList)
                             .filter(new Func1<UriFilter, Boolean>() {
                                 @Override
                                 public Boolean call(UriFilter uriFilter) {
                                     return uriFilter.getFilter() == filter;
                                 }
                             })
                             .first()
                             .toSingle();
        }
    }

    @Override
    public void updateFilter(String old, String update) {
        localDataSource.updateFilter(old, update);
        for (int i = 0; i < cachedList.size(); i++) {
            if (cachedList.get(i).getFilter().equals(old)) {
                cachedList.get(i).setFilter(update);
                return;
            }
        }
    }

    @Override
    public void deleteFilter(String delete) {
        localDataSource.deleteFilter(delete);
        for (int i = 0; i < cachedList.size(); i++) {
            if (cachedList.get(i).getFilter().equals(delete)) {
                cachedList.remove(i);
                return;
            }
        }
    }
}
