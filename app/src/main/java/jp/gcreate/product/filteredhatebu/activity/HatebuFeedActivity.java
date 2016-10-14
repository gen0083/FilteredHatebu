package jp.gcreate.product.filteredhatebu.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.MyApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ActivityFeedBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.fragment.HatebuFeedFragmentsAdapter;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedActivity extends AppCompatActivity {
    private ActivityFeedBinding binding;
    private ActivityComponent component;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed);

        component = MyApplication.getActivityComponent(this);

        List<String> categoryKeys = new ArrayList<>();
        categoryKeys.add("it");
        categoryKeys.add("life");
        categoryKeys.add("game");
        HatebuFeedFragmentsAdapter adapter = new HatebuFeedFragmentsAdapter(getSupportFragmentManager(), categoryKeys);
        binding.viewPager.setAdapter(adapter);
        binding.viewPagerTitle.setupWithViewPager(binding.viewPager);
    }
}