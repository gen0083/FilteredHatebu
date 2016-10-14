package jp.gcreate.product.filteredhatebu.data;

import java.util.List;

import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Single;

/**
 * Copyright 2016 G-CREATE
 */

public interface FilterDataSource {
    void insertFilter(String insert);

    Single<List<UriFilter>> getFilterAll();

    Single<UriFilter> getFilter(String filter);

    void updateFilter(String old, String update);

    void deleteFilter(String delete);
}
