package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedFragmentsAdapter extends FragmentStatePagerAdapter {
    private HatebuFeedActivityPresenter presenter;

    public HatebuFeedFragmentsAdapter(FragmentManager fm, HatebuFeedActivityPresenter presenter) {
        super(fm);
        this.presenter = presenter;
    }

    @Override
    public Fragment getItem(int position) {
        return presenter.getItem(position);
    }

    @Override
    public int getCount() {
        return presenter.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return presenter.getPageTitle(position);
    }
}
