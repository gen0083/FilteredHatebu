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
    // TODO: use string now but replace HatebuFeedChannel for the future.
    private List<String> keys;
    private HashMap<String, HatebuFeedFragment> fragments;

    public HatebuFeedFragmentsAdapter(FragmentManager fm) {
        this(fm, new ArrayList<String>());
    }

    public HatebuFeedFragmentsAdapter(FragmentManager fm, List<String> keys) {
        super(fm);
        this.keys = keys;
        fragments = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        final String key = keys.get(position);
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
        return keys.get(position);
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public void addKey(String key) {
        keys.add(key);
    }
}
