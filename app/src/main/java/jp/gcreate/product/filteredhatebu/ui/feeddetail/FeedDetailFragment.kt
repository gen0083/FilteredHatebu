package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
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
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper
import jp.gcreate.product.filteredhatebu.ui.common.LoadingState
import timber.log.Timber
import javax.inject.Inject

class FeedDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentFeedDetailBinding
    lateinit var vm: FeedDetailViewModel
    @Inject lateinit var viewModelFactory: ViewModelProviderFactory
    @Inject lateinit var customTabHelper: CustomTabHelper
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(viewModelFactory)
        
        val url = FeedDetailFragmentArgs.fromBundle(arguments).feedUrl
        setupActionsFromView()
        subscribeViewModel()
        
        vm.fetchFeed(url)
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
            val isLoading = it == LoadingState.LOADING
            binding.progressBar.isVisible = isLoading
            binding.commentStatusMessage.isGone = isLoading
        })
        vm.addFilterAction.observe(this, Observer {
            it?.handleEvent()?.let {
                Timber.d("add filter $it")
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
        binding.openCommentDialog.setOnClickListener {
            CommentBottomSheetDialog().show(fragmentManager, "comments")
        }
    }
    
    private fun handleComments(comments: HatebuComments) {
        binding.progressBar.isGone = true
        binding.commentStatusMessage.isVisible = true
        binding.commentStatusMessage.text = when (comments) {
            is HatebuComments.Error    -> getString(R.string.fetch_comment_error)
            is HatebuComments.Disallow -> getString(R.string.disallow_comments)
            is HatebuComments.Empty    -> getString(R.string.no_comments)
            is HatebuComments.Comments -> getString(R.string.got_comments, comments.comments.size)
        }
    }
}