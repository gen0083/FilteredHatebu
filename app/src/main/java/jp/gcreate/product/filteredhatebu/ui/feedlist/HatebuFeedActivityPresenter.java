package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;
import jp.gcreate.product.filteredhatebu.domain.usecase.GetFilteredFeedList;
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
public class HatebuFeedActivityPresenter implements HatebuFeedContract.ParentPresenter {
    private HatebuFeedContract.ActivityView activity;
    private List<HatebuCategory>                         keys        = new ArrayList<>();
    private HashMap<String, HatebuFeedFragmentPresenter> presenters  = new HashMap<>();
    private boolean                                      isFirstTime = true;
    private GetFilteredFeedList getFilteredFeedList;
    private FilterRepository    filterRepository;
    private FaviconUtil         faviconUtil;

    @Inject
    public HatebuFeedActivityPresenter(GetFilteredFeedList getFilteredFeedList, FilterRepository filterRepository, FaviconUtil faviconUtil) {
        this.getFilteredFeedList = getFilteredFeedList;
        this.filterRepository = filterRepository;
        this.faviconUtil = faviconUtil;
    }

    @Override
    public void onAttach(HatebuFeedContract.ActivityView activity) {
        this.activity = activity;
        if (isFirstTime) {
            keys.add(new HatebuCategory("", "総合"));
            keys.add(new HatebuCategory("general", "一般"));
            keys.add(new HatebuCategory("it", "テクノロジー"));
            keys.add(new HatebuCategory("life", "暮らし"));
            keys.add(new HatebuCategory("game", "アニメとゲーム"));
            isFirstTime = false;
            activity.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        this.activity = null;
    }

    @Override
    public HatebuFeedContract.ChildPresenter getOrCreateFragmentPresenter(String key) {
        if (presenters.containsKey(key)) {
            return presenters.get(key);
        } else {
            HatebuFeedFragmentPresenter p = new HatebuFeedFragmentPresenter(key,
                                                                            getFilteredFeedList, filterRepository, faviconUtil);
            presenters.put(key, p);
            return p;
        }
    }

    @Override
    public void reloadFeeds(int position) {
        presenters.get(getKey(position)).reloadList();
    }

    private String getKey(int position) {
        return keys.get(position).key;
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d("pager adapter requested getItem(%d)", position);
        String key = getKey(position);
        return HatebuFeedFragment.createInstance(key);
    }

    @Override
    public String getPageTitle(int position) {
        return keys.get(position).title;
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @VisibleForTesting
    void initialzieFilterRepository() {
        filterRepository.deleteAll();
    }

    private static class HatebuCategory {
        String key;
        public String title;

        HatebuCategory(String key, String title) {
            this.key = key;
            this.title = title;
        }
    }
}
