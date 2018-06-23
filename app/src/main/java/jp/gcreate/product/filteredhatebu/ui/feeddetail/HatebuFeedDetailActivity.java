package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.CustomApplication;
import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ActivityHatebuFeedDetailBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuFeedDetailActivity extends AppCompatActivity
        implements SelectFilterDialogFragment.Callback, HatebuFeedDetailContract.View {
    private static final String EXTRA_ITEM_KEY = "feed_item_key";
    private static final String EXTRA_BOTTOM_SHEET_STATE_KEY = "bottom_sheet_state";
    private static final String EXTRA_COMMENT_POSITION_KEY = "comments_position";
    private ActivityHatebuFeedDetailBinding binding;
    private HatebuFeedItem item;
    private ActivityComponent component;
    private LinearLayoutManager layoutManager;
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    @Inject
    HatebuFeedDetailPresenter presenter;
    @Inject
    BookmarkCommentsAdapter commentsAdapter;
    @Inject
    CustomTabHelper customTabHelper;

    public static Intent createIntent(Context context, HatebuFeedItem item) {
        Intent i = new Intent(context, HatebuFeedDetailActivity.class);
        i.putExtra(EXTRA_ITEM_KEY, item);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hatebu_feed_detail);

        component = CustomApplication.getActivityComponent(this);
        component.inject(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ITEM_KEY)) {
            item = intent.getParcelableExtra(EXTRA_ITEM_KEY);
        } else {
            throw new RuntimeException("This activity must set HatebuFeedItem.");
        }

        setupRecyclerView();

        binding.readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customTabHelper.openCustomTab(item.getLink());
            }
        });
        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareUrl(item.getLink());
            }
        });
        binding.addFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView r = binding.recyclerView;
        layoutManager = new LinearLayoutManager(this);
        r.setLayoutManager(layoutManager);
        r.setAdapter(commentsAdapter);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.setItem(item);
        presenter.onAttach(this, item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onDetach();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_COMMENT_POSITION_KEY,
                        layoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putInt(EXTRA_BOTTOM_SHEET_STATE_KEY, bottomSheetBehavior.getState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(EXTRA_COMMENT_POSITION_KEY, 0);
            int state = savedInstanceState
                    .getInt(EXTRA_BOTTOM_SHEET_STATE_KEY, BottomSheetBehavior.STATE_COLLAPSED);
            layoutManager.scrollToPosition(position);
            bottomSheetBehavior.setState(state);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void shareUrl(String url) {
        startActivity(createShareUrlIntent(url));
    }

    private Intent createShareUrlIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        return intent;
    }

    private void openFilterDialog() {
        SelectFilterDialogFragment f = SelectFilterDialogFragment.newInstance(item.getLink());
        f.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onCanceled() {
        Timber.d("%s onCanceled from AlertDialog.", this);
    }

    @Override
    public void onSelected(final String selected) {
        Timber.d("%s onSelected from AlertDialog selected:%s", this, selected);
        presenter.addFilter(selected);
        Snackbar.make(binding.title, getString(R.string.add_filter_done, selected),
                      Snackbar.LENGTH_SHORT)
                .setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.cancelAddedFilter();
                    }
                })
                .show();
    }

    @Override
    public void showLoading() {
        Timber.d("loading comments");
        binding.commentStatus.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        Timber.d("finish loading comments");
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNoComments() {
        Timber.d("No comments");
        binding.commentStatus.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void notifyDataSetChanged() {
        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        commentsAdapter.notifyItemChanged(position);
    }

    @Override
    public void notifyItemInserted(int position) {
        commentsAdapter.notifyItemInserted(position);
    }

    @VisibleForTesting
    boolean isCommentLoadFinished() {
        return !binding.progressBar.isShown();
    }
}
