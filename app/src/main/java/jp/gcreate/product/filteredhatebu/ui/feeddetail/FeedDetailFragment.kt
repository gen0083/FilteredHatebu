package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.arch.lifecycle.Observer
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
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedDetailBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper
import jp.gcreate.product.filteredhatebu.ui.common.LoadingState
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
        vm = injectViewModel(viewModelFactory)
        
        val url = FeedDetailFragmentArgs.fromBundle(arguments).feedUrl
        setupRecyclerView()
        setupActionsFromView()
        subscribeViewModel()
        
        vm.fetchFeed(url)
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
        vm.addFilterAction.observe(this, Observer {
            it?.handleEvent()?.let {
                Timber.d("add filter $it")
                showSnackbar(R.string.add_filter_done)
                findNavController().popBackStack()
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
            PickFilterDialogFragment()
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