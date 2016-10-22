package jp.gcreate.product.filteredhatebu.data;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */
public class FilterRepository implements FilterDataSource {
    private List<UriFilter> cachedList = new ArrayList<>();
    private boolean         isDirty    = true;
    private FilterDataSource localDataSource;
    private SerializedSubject<Long, Long> onModifiedObserver = new SerializedSubject<>(
            BehaviorSubject.<Long>create());

    public FilterRepository(FilterDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public void insertFilter(String insert) {
        localDataSource.insertFilter(insert);
        isDirty = true;
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
        isDirty = true;
        onModifiedObserver.onNext(System.currentTimeMillis());
    }

    @Override
    public void deleteFilter(String delete) {
        localDataSource.deleteFilter(delete);
        isDirty = true;
        onModifiedObserver.onNext(System.currentTimeMillis());
    }

    public Observable<Long> listenModified() {
        return onModifiedObserver;
    }
}
