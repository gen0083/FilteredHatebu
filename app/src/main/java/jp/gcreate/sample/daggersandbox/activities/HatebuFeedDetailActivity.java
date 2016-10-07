package jp.gcreate.sample.daggersandbox.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import jp.gcreate.sample.daggersandbox.R;
import jp.gcreate.sample.daggersandbox.databinding.ActivityHatebuFeedDetailBinding;
import jp.gcreate.sample.daggersandbox.model.HatebuFeedItem;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedDetailActivity extends AppCompatActivity {
    private static final String EXTRA_ITEM_KEY = "feed_item_key";
    private ActivityHatebuFeedDetailBinding binding;
    private HatebuFeedItem                  item;

    public static Intent createIntent(Context context, HatebuFeedItem item) {
        Intent i = new Intent(context, HatebuFeedDetailActivity.class);
        i.putExtra(EXTRA_ITEM_KEY, item);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hatebu_feed_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ITEM_KEY)) {
            item = intent.getParcelableExtra(EXTRA_ITEM_KEY);
        } else {
            throw new RuntimeException("This activity must set HatebuFeedItem.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setItem(item);
    }
}
