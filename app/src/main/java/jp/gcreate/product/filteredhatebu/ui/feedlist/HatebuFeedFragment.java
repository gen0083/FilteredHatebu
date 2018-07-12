package jp.gcreate.product.filteredhatebu.ui.feedlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.FragmentHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.feeddetail.HatebuFeedDetailActivity;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedFragment extends Fragment implements HatebuFeedContract.FragmentView {
    private static final String EXTRA_CATEGORY_KEY          = "category_key";
    private static final String EXTRA_SCROLLED_POSITION_KEY = "scrolled_position";
    private FragmentHatebuFeedBinding binding;
    private String                    categoryKey;
    private FeedAdapter               adapter;
    private LinearLayoutManager       layoutManager;
    private int                       scrolledPosition;
//    @Inject
    HatebuFeedActivityPresenter parentPresenter;
//    @Inject
    OkHttpClient client;
    private HatebuFeedContract.ChildPresenter presenter;


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
        Bundle args = getArguments();
        if (args.containsKey(EXTRA_CATEGORY_KEY)) {
            categoryKey = args.getString(EXTRA_CATEGORY_KEY);
        } else {
            throw new RuntimeException("This fragment must set category key.");
        }
        Timber.d("%s[category:%s] onAttach to %s", this, categoryKey, context.toString());
        if (context instanceof Activity) {
//            CustomApplication.Companion.getActivityComponent((Activity) context).inject(this);
            presenter = parentPresenter.getOrCreateFragmentPresenter(categoryKey);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.d("%s[category:%s] onDetach", this, categoryKey);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("%s[category:%s] onCreateView", this, categoryKey);
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_hatebu_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("%s[category:%s] onDestroyView", this, categoryKey);
        binding.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("%s[category:%s] onActivityCreated", this, categoryKey);
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
        Timber.d("%s[category:%s] onResume presenter:%d", this, categoryKey, presenter.hashCode());
        presenter.onAttach(this);
        layoutManager.scrollToPosition(scrolledPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("%s[category:%s] onPause", this, categoryKey);
        presenter.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("%s[category:%s] onSaveInstanceState", this, categoryKey);
        scrolledPosition = layoutManager.findFirstVisibleItemPosition();
        outState.putInt(EXTRA_SCROLLED_POSITION_KEY, scrolledPosition);
    }

    @VisibleForTesting
    RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @VisibleForTesting
    boolean isLoading() {
        return binding.progressBar.isShown();
    }

    @Override
    public void showLoading() {
        Timber.d("start loading animation");
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        Timber.d("stop loading animation");
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void notifyFilterUpdated() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyNewContentsFetched() {
        adapter.notifyDataSetChanged();
        layoutManager.smoothScrollToPosition(binding.recyclerView, null, 0);
        Snackbar.make(binding.getRoot(), R.string.new_contents_fetched, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkError() {
        Timber.e("Network error");
    }

    @Override
    public void notifyNewContentsDoseNotExist() {
        Timber.d("New contents dose not exist.");
        Snackbar.make(binding.getRoot(), R.string.new_contents_dose_not_exist, Snackbar.LENGTH_SHORT).show();
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
}
