package jp.gcreate.product.filteredhatebu.ui.editfilter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ActivityFilterEditBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;

/**
 * Copyright 2016 G-CREATE
 */

public class FilterEditActivity extends AppCompatActivity implements FilterEditContract.View {
    private ActivityFilterEditBinding binding;
    private ActivityComponent component;
//    @Inject
    FilterEditPresenter presenter;
    private FilterAdapter filterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_edit);

//        component = CustomApplication.Companion.getActivityComponent(this);
//        component.inject(this);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        filterAdapter = new FilterAdapter(this, presenter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(filterAdapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onAttach(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onDetach();
    }

    @Override
    public void notifyDatasetChanged() {
        filterAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        filterAdapter.notifyItemChanged(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        filterAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position) {
        filterAdapter.notifyItemInserted(position);
    }
}
