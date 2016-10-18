package jp.gcreate.product.filteredhatebu.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.MyApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.ActivityFilterEditBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import jp.gcreate.product.filteredhatebu.recycler.FilterAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Copyright 2016 G-CREATE
 */

public class FilterEditActivity extends AppCompatActivity {
    private ActivityFilterEditBinding binding;
    private ActivityComponent component;
    @Inject
    FilterRepository filterRepository;
    private FilterAdapter filterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_edit);

        component = MyApplication.getActivityComponent(this);
        component.inject(this);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        filterAdapter = new FilterAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(filterAdapter);
        filterRepository.getFilterAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<UriFilter>>() {
                            @Override
                            public void call(List<UriFilter> uriFilters) {
                                filterAdapter.setList(uriFilters);
                            }
                        });

    }
}
