package jp.gcreate.product.filteredhatebu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.CustomApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.activity.HatebuFeedDetailActivity;
import jp.gcreate.product.filteredhatebu.api.HatebuHotentryCategoryService;
import jp.gcreate.product.filteredhatebu.api.HatebuHotentryService;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.databinding.FragmentHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.recycler.FeedAdapter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedFragment extends Fragment implements FeedAdapter.OnRecycelerItemClickListener {
    private static final String EXTRA_CATEGORY_KEY = "category_key";
    private FragmentHatebuFeedBinding binding;
    private String                    categoryKey;
    private Subscription              serviceSubscription;
    private FeedAdapter               adapter;
    private LinearLayoutManager       layoutManager;
    private int                       scrolledPosition;
    @Inject
    HatebuHotentryService         hotentryService;
    @Inject
    HatebuHotentryCategoryService categoryService;
    @Inject
    FilterRepository              filterRepository;

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
            CustomApplication.getActivityComponent((Activity) context).inject(this);
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
        adapter = new FeedAdapter(context, filterRepository);
        adapter.setOnRecyclerItemClickListener(this);
        layoutManager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("%s onStart", this.toString());
        Observable<HatebuFeed> feed;
        if (categoryKey.equals("")) {
            feed = hotentryService.getHotentryFeed();
        } else {
            feed = categoryService.getCategoryFeed(categoryKey);
        }
        serviceSubscription = feed
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HatebuFeed>() {
                    @Override
                    public void call(HatebuFeed hatebuFeed) {
                        // HatebuFeedはレスポンス本体で、実際に必要な各記事のリストはgetItemListで取得する
                        Timber.d("feed got: %d", hatebuFeed.getItemList().size());
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

    @Override
    public void onResume() {
        super.onResume();
        layoutManager.scrollToPosition(scrolledPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        scrolledPosition = layoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public void onClick(FeedAdapter adapter, int position, HatebuFeedItem item) {
        Intent i = HatebuFeedDetailActivity.createIntent(getActivity(), item);
        getActivity().startActivity(i);
    }
}
