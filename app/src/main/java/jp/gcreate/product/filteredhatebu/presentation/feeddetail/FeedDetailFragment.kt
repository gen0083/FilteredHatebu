package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedDetailBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper
import jp.gcreate.product.filteredhatebu.ui.common.LoadingState
import jp.gcreate.product.filteredhatebu.ui.common.PickFilterDialogFragment
import timber.log.Timber
import javax.inject.Inject

class FeedDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentFeedDetailBinding
    lateinit var vm: FeedDetailViewModel
    @Inject lateinit var viewModelFactory: ViewModelProviderFactory
    @Inject lateinit var feedCommentsAdapter: FeedCommentsAdapter
    @Inject lateinit var customTabHelper: CustomTabHelper
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProviders.of(activity!!, viewModelFactory)[FeedDetailViewModel::class.java]
        val url = FeedDetailFragmentArgs.fromBundle(arguments).feedUrl
        setupRecyclerView()
        setupActionsFromView()
        subscribeViewModel()
        
        vm.fetchFeed(url)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PickFilterDialogFragment.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val filter = it.getStringExtra(Intent.EXTRA_TEXT)
                    Timber.d("got $filter from dialog fragment through intent:$it")
                    vm.addFilter(filter)
                    showSnackbar(R.string.add_filter_done)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = feedCommentsAdapter
        }
    }
    
    private fun subscribeViewModel() {
        vm.feedDetail.observe(this, Observer {
            it?.let {
                Timber.d("detail feed: $it")
                binding.item = it
            }
        })
        vm.hatebuComments.observe(this, Observer {
            if (it == null) return@Observer
            handleComments(it)
        })
        vm.loadingState.observe(this, Observer {
            Timber.d("loading state $it")
            if (it == LoadingState.LOADING) {
                binding.progressBar.isVisible = true
                binding.commentStatus.isGone = true
                binding.recyclerView.isGone = true
            }
        })
    }
    
    private fun setupActionsFromView() {
        binding.shareButton.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, vm.currentUrl)
            })
        }
        binding.readMoreButton.setOnClickListener {
            customTabHelper.openCustomTab(vm.currentUrl)
        }
        binding.addFilterButton.setOnClickListener {
            PickFilterDialogFragment.createDialog(this, vm.currentUrl)
                .show(fragmentManager, vm.currentUrl)
        }
    }
    
    private fun handleComments(comments: HatebuComments) {
        when(comments) {
            is HatebuComments.Error -> showSnackbar(R.string.fetch_comment_error)
            is HatebuComments.Disallow -> showStatusMessage(R.string.disallow_comments)
            is HatebuComments.Empty -> showStatusMessage(R.string.no_comments)
            is HatebuComments.Comments -> showComments(comments.comments)
        }
    }
    
    private fun showComments(comments: List<HatebuBookmark>) {
        Timber.d("show comments ${comments.size}")
        binding.progressBar.isGone = true
        binding.commentStatus.isGone = true
        binding.recyclerView.isVisible = true
        feedCommentsAdapter.submitList(comments)
        linearLayoutManager.scrollToPosition(0)
    }
    
    private fun showStatusMessage(@StringRes messageStringRes: Int) {
        Timber.d("show message why no comments")
        binding.progressBar.isGone = true
        binding.recyclerView.isGone = true
        binding.commentStatus.isVisible = true
        binding.commentStatusMessage.setText(messageStringRes)
        feedCommentsAdapter.submitList(emptyList())
    }
    
    private fun showSnackbar(@StringRes stringRes: Int) {
        Snackbar.make(binding.root, stringRes, Snackbar.LENGTH_SHORT).show()
    }
}