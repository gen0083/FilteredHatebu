package jp.gcreate.sample.daggersandbox.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.sample.daggersandbox.MyApplication;
import jp.gcreate.sample.daggersandbox.R;
import jp.gcreate.sample.daggersandbox.databinding.ActivityFeedBinding;
import jp.gcreate.sample.daggersandbox.di.ActivityComponent;
import jp.gcreate.sample.daggersandbox.fragments.HatebuFeedFragmentsAdapter;

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
        HatebuFeedFragmentsAdapter adapter = new HatebuFeedFragmentsAdapter(getSupportFragmentManager(), categoryKeys);
        binding.viewPager.setAdapter(adapter);
    }
}
