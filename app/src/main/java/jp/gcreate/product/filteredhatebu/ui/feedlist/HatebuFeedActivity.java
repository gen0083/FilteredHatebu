package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.CustomApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ActivityFeedBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.ui.editfilter.FilterEditActivity;
import jp.gcreate.product.filteredhatebu.ui.option.LicensesDialogFragment;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedActivity extends AppCompatActivity implements HatebuFeedContract.ActivityView {
    private static final String TAG_LICENSE = "license";
    private ActivityFeedBinding binding;
    private ActivityComponent component;
    private HatebuFeedFragmentsAdapter adapter;
    @Inject
    HatebuFeedActivityPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed);

        component = CustomApplication.getActivityComponent(this);
        component.inject(this);

        setSupportActionBar(binding.toolbar);

        adapter = new HatebuFeedFragmentsAdapter(getSupportFragmentManager(), presenter);
        binding.viewPager.setAdapter(adapter);
        binding.viewPagerTitle.setupWithViewPager(binding.viewPager);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_filter:
                Intent intent = new Intent(this, FilterEditActivity.class);
                startActivity(intent);
                return true;
            case R.id.licenses:
                new LicensesDialogFragment().show(getSupportFragmentManager(), TAG_LICENSE);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @VisibleForTesting
    RecyclerView getRecyclerView() {
        return ((HatebuFeedFragment)adapter.getItem(binding.viewPager.getCurrentItem())).getRecyclerView();
    }

    @VisibleForTesting
    PagerAdapter getPagerAdapter() {
        return adapter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}
