package jp.gcreate.product.filteredhatebu.ui.editfilter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.ItemFilterBinding;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;
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
        }
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
        deletePrevious();
        deletedItem = new DeletedITem<>(position, list.get(position));
        if (view != null) {
            view.notifyItemChanged(position);
        }
    }

    private void deletePrevious() {
        if (deletedItem == null) return;
        int position = deletedItem.getPosition();
        list.remove(position);
        filterRepository.deleteFilter(deletedItem.getItem().getFilter());
        if (view != null) {
            view.notifyItemRemoved(position);
        }
        deletedItem = null;
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
    public void onBindViewHolder(DataBindingViewHolder<ItemFilterBinding> holder, int position) {
        ItemFilterBinding binding = holder.getBinding();
        UriFilter filter = list.get(position);
        boolean isDeleted = (deletedItem != null && deletedItem.getPosition() == position);
        binding.setItem(filter);
        binding.setIsDeleted(isDeleted);
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoDelete();
            }
        });
    }
}
