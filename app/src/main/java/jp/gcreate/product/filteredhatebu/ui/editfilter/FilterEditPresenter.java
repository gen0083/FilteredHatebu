package jp.gcreate.product.filteredhatebu.ui.editfilter;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import jp.gcreate.product.filteredhatebu.ui.common.DeletedITem;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Copyright 2016 G-CREATE
 */
@ActivityScope
public class FilterEditPresenter implements FilterEditContract.Presenter {
    private FilterEditContract.View view;
    private FilterRepository filterRepository;
    private List<UriFilter> list = new ArrayList<>();
    private long previousUpdate;
    private boolean isInitialize = false;
    private DeletedITem<UriFilter> deletedItem;

    @Inject
    public FilterEditPresenter(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
        filterRepository.listenModified()
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (aLong > previousUpdate) {
                            updateList();
                            previousUpdate = aLong;
                        }
                    }
                });
    }

    @Override
    public void onAttach(FilterEditContract.View view) {
        this.view = view;
        if (!isInitialize) {
            updateList();
            isInitialize = true;
        }
        view.notifyDatasetChanged();
    }

    private void updateList() {
        filterRepository.getFilterAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<UriFilter>>() {
                    @Override
                    public void call(List<UriFilter> uriFilters) {
                        list = uriFilters;
                        if (view != null) {
                            view.notifyDatasetChanged();
                        }
                    }
                });
    }

    @Override
    public void onDetach() {
        view = null;
        deletePrevious();
    }

    @Override
    public void delete(int position) {
        int previous = deletePrevious();
        // if position bigger than previous one it produce IndexOutBoundsException
        if (previous <= position) position--;
        deletedItem = new DeletedITem<>(position, list.get(position));
        if (view != null) {
            view.notifyItemChanged(position);
        }
    }

    private int deletePrevious() {
        if (deletedItem == null) return Integer.MAX_VALUE;
        int position = deletedItem.getPosition();
        list.remove(position);
        filterRepository.deleteFilter(deletedItem.getItem().getFilter());
        if (view != null) {
            view.notifyItemRemoved(position);
        }
        deletedItem = null;
        return position;
    }

    @Override
    public void undoDelete() {
        if (deletedItem == null) return;
        int position = deletedItem.getPosition();
        if (view != null) {
            view.notifyItemChanged(position);
        }
        deletedItem = null;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public UriFilter getItem(int position) {
        return list.get(position);
    }

    @Override
    public boolean isDeleted(int position) {
        return (deletedItem != null && deletedItem.getPosition() == position);
    }

    @VisibleForTesting
    List<UriFilter> getList() {
        return list;
    }
}
