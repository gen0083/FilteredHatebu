package jp.gcreate.product.filteredhatebu.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedFragmentsAdapter extends FragmentStatePagerAdapter {
    private List<HatebuCategory> keys;
    private HashMap<String, HatebuFeedFragment> fragments;

    public HatebuFeedFragmentsAdapter(FragmentManager fm) {
        this(fm, new ArrayList<HatebuCategory>());
    }

    public HatebuFeedFragmentsAdapter(FragmentManager fm, List<HatebuCategory> keys) {
        super(fm);
        this.keys = keys;
        fragments = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        final String key = keys.get(position).key;
        if (fragments.containsKey(key)) {
            return fragments.get(key);
        } else {
            HatebuFeedFragment f = HatebuFeedFragment.createInstance(key);
            fragments.put(key, f);
            return f;
        }
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return keys.get(position).title;
    }

    public void setKeys(List<HatebuCategory> categories) {
        this.keys = categories;
    }

    public void addKey(HatebuCategory category) {
        keys.add(category);
    }

    public static class HatebuCategory {
        public String key;
        public String title;

        public HatebuCategory(String key, String title) {
            this.key = key;
            this.title = title;
        }
    }
}
