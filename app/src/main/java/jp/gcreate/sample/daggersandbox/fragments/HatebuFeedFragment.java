package jp.gcreate.sample.daggersandbox.fragments;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import jp.gcreate.sample.daggersandbox.MyApplication;
import jp.gcreate.sample.daggersandbox.R;
import jp.gcreate.sample.daggersandbox.api.HatebuFeedService;
import jp.gcreate.sample.daggersandbox.databinding.FragmentHatebuFeedBinding;
import jp.gcreate.sample.daggersandbox.model.HatebuFeed;
import jp.gcreate.sample.daggersandbox.recycler.FeedAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedFragment extends Fragment {
    private static final String EXTRA_CATEGORY_KEY = "category_key";
    private FragmentHatebuFeedBinding binding;
    private String                    categoryKey;
    private Subscription              serviceSubscription;
    private FeedAdapter               adapter;
    @Inject
    HatebuFeedService service;

    public static HatebuFeedFragment createInstance(String category) {
        HatebuFeedFragment f    = new HatebuFeedFragment();
        Bundle             args = new Bundle();
        args.putString(EXTRA_CATEGORY_KEY, category);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d("%s onAttach to %s", this.toString(), context.toString());
        if (context instanceof Activity) {
            MyApplication.getActivityComponent((Activity) context).inject(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.d("%s onDetach", this.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("%s onCreateView", this);
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_hatebu_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("%s onDestroyView", this);
        binding.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("%s onActivityCreated", this);
        Bundle args = getArguments();
        if (args.containsKey(EXTRA_CATEGORY_KEY)) {
            categoryKey = args.getString(EXTRA_CATEGORY_KEY);
        } else {
            throw new RuntimeException("This fragment must set category key.");
        }
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Context context = getContext();
        adapter = new FeedAdapter(context);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("%s onStart", this.toString());
        serviceSubscription = service.getCategoryFeed(categoryKey)
                                     .subscribeOn(Schedulers.io())
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(new Action1<HatebuFeed>() {
                                         @Override
                                         public void call(HatebuFeed hatebuFeed) {
                                             // set feed
                                             adapter.setItemList(hatebuFeed.getItemList());
                                         }
                                     }, new Action1<Throwable>() {
                                         @Override
                                         public void call(Throwable throwable) {
                                             Timber.e("error %s", throwable.getMessage());
                                             throwable.printStackTrace();
                                         }
                                     });
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("%s onStop", this.toString());
        if (serviceSubscription != null && !serviceSubscription.isUnsubscribed()) {
            serviceSubscription.unsubscribe();
        }
    }
}
