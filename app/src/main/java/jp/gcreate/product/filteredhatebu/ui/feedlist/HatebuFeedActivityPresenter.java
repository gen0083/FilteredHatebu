package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet;
import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope;

/**
 * Copyright 2016 G-CREATE
 */

@ActivityScope
public class HatebuFeedActivityPresenter implements HatebuFeedContract.ParentPresenter {
    private HatebuFeedContract.ActivityView activity;
    private List<HatebuCategory>                         keys        = new ArrayList<>();
    private HashMap<String, HatebuFeedFragment>          fragments   = new HashMap<>();
    private HashMap<String, HatebuFeedFragmentPresenter> presenters  = new HashMap<>();
    private boolean                                      isFirstTime = true;
    private FeedsBurnerClienet      feedsBurnerClienet;
    private HatenaClient.XmlService hatenaXmlService;
    private FilterRepository        filterRepository;

    @Inject
    public HatebuFeedActivityPresenter(FeedsBurnerClienet feedsBurnerClienet,
                                       HatenaClient.XmlService hatenaXmlService,
                                       FilterRepository filterRepository) {
        this.feedsBurnerClienet = feedsBurnerClienet;
        this.hatenaXmlService = hatenaXmlService;
        this.filterRepository = filterRepository;
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
        }
        activity.notifyDataSetChanged();
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
                                                                            feedsBurnerClienet,
                                                                            hatenaXmlService,
                                                                            filterRepository);
            presenters.put(key, p);
            return p;
        }
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

    public static class HatebuCategory {
        public String key;
        public String title;

        public HatebuCategory(String key, String title) {
            this.key = key;
            this.title = title;
        }
    }
}
