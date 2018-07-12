package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ActivityFeedBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.ui.editfilter.FilterEditActivity;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedActivity extends AppCompatActivity implements HatebuFeedContract.ActivityView {
    private static final String TAG_LICENSE = "license";
    private ActivityFeedBinding binding;
    private ActivityComponent component;
    private HatebuFeedFragmentsAdapter adapter;
//    @Inject
    HatebuFeedActivityPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed);

//        component = CustomApplication.Companion.getActivityComponent(this);
//        component.inject(this);

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
            case R.id.reload_feeds:
                presenter.reloadFeeds(binding.viewPager.getCurrentItem());
                return true;
            case R.id.edit_filter:
                Intent intent = new Intent(this, FilterEditActivity.class);
                startActivity(intent);
                return true;
            case R.id.licenses:
                startActivity(new Intent(this, OssLicensesMenuActivity.class));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private HatebuFeedFragment getCurrentFragment() {

        return (HatebuFeedFragment) adapter.instantiateItem(binding.viewPager,
                                                            binding.viewPager.getCurrentItem());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void notifyDataSetChanged() {
        Timber.d("notify view pager adapter's data changed.");
        adapter.notifyDataSetChanged();
    }

    @VisibleForTesting
    boolean isFeedLoading() {
        return getCurrentFragment().isLoading();
    }
}
