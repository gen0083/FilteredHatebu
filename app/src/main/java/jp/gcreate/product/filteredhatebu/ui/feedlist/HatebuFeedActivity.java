package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.CustomApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.ActivityFeedBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.ui.option.LicensesDialogFragment;
import jp.gcreate.product.filteredhatebu.ui.editfilter.FilterEditActivity;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedActivity extends AppCompatActivity {
    private static final String TAG_LICENSE = "license";
    private ActivityFeedBinding binding;
    private ActivityComponent component;
    @Inject
    FilterRepository filterRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed);

        component = CustomApplication.getActivityComponent(this);
        component.inject(this);

        setSupportActionBar(binding.toolbar);

        List<HatebuFeedFragmentsAdapter.HatebuCategory> categoryKeys = new ArrayList<>();
        categoryKeys.add(new HatebuFeedFragmentsAdapter.HatebuCategory("", "総合"));
        categoryKeys.add(new HatebuFeedFragmentsAdapter.HatebuCategory("general", "一般"));
        categoryKeys.add(new HatebuFeedFragmentsAdapter.HatebuCategory("it", "テクノロジー"));
        categoryKeys.add(new HatebuFeedFragmentsAdapter.HatebuCategory("life", "暮らし"));
        categoryKeys.add(new HatebuFeedFragmentsAdapter.HatebuCategory("game", "アニメとゲーム"));
        HatebuFeedFragmentsAdapter adapter = new HatebuFeedFragmentsAdapter(getSupportFragmentManager(), categoryKeys);
        binding.viewPager.setAdapter(adapter);
        binding.viewPagerTitle.setupWithViewPager(binding.viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
