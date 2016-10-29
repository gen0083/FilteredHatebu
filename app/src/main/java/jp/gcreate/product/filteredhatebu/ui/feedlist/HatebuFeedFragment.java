package jp.gcreate.product.filteredhatebu.ui.feedlist;

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
import jp.gcreate.product.filteredhatebu.databinding.FragmentHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.feeddetail.HatebuFeedDetailActivity;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedFragment extends Fragment implements HatebuFeedContract.Fragment.View {
    private static final String EXTRA_CATEGORY_KEY = "category_key";
    private static final String EXTRA_SCROLLED_POSITION_KEY = "scrolled_position";
    private FragmentHatebuFeedBinding                     binding;
    private String                                        categoryKey;
    private FeedAdapter                                   adapter;
    private LinearLayoutManager                           layoutManager;
    private int                                           scrolledPosition;
    @Inject
            HatebuFeedActivityPresenter activityPresenter;
    private HatebuFeedContract.Fragment.FragmentPresenter presenter;


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
            Bundle args = getArguments();
            if (args.containsKey(EXTRA_CATEGORY_KEY)) {
                categoryKey = args.getString(EXTRA_CATEGORY_KEY);
            } else {
                throw new RuntimeException("This fragment must set category key.");
            }
            presenter = activityPresenter.getOrCreateFragmentPresenter(categoryKey);
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
        setupRecyclerView();
        if (savedInstanceState != null) {
            scrolledPosition = savedInstanceState.getInt(EXTRA_SCROLLED_POSITION_KEY, 0);
        }
    }

    private void setupRecyclerView() {
        Context context = getContext();
        adapter = new FeedAdapter(context, presenter);
        layoutManager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("%s[category:%s] onResume presenter:%s", this, categoryKey, presenter);
        presenter.onAttach(this);
        layoutManager.scrollToPosition(scrolledPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("%s onPause", this);
        scrolledPosition = layoutManager.findFirstVisibleItemPosition();
        presenter.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_SCROLLED_POSITION_KEY, scrolledPosition);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showNetworkError() {
        Timber.e("Network error");
    }

    @Override
    public void showNewContentsDoseNotExist() {
        Timber.d("New contents dose not exist.");
    }

    @Override
    public void launchFeedDetailActivity(HatebuFeedItem item) {
        Intent i = HatebuFeedDetailActivity.createIntent(getActivity(), item);
        getActivity().startActivity(i);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        adapter.notifyItemChanged(position);
    }
}
