package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedDetailBinding
import jp.gcreate.product.filteredhatebu.ui.common.CustomTabHelper
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class FeedDetailFragment : Fragment() {
    private lateinit var binding: FragmentFeedDetailBinding
    private val vm: FeedDetailViewModel by sharedViewModel()
    private val customTabHelper: CustomTabHelper by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val url = FeedDetailFragmentArgs.fromBundle(arguments).feedUrl
        setupActionsFromView()
        subscribeViewModel()
        
        vm.fetchFeed(url)
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.feed_detail_menu, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_filter -> {
            showFilterCandidate()
            true
        }
        
        R.id.share_url  -> {
            startActivity(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, vm.currentUrl)
            })
            true
        }
        
        else            -> super.onOptionsItemSelected(item)
    }
    
    private fun subscribeViewModel() {
        vm.feedDetail.observe(this, Observer {
            it?.let {
                Timber.d("detail feed: $it")
                binding.item = it
            }
        })
        vm.hatebuComments.observe(this, Observer {
            it?.let {
                binding.commentButton.setCommentStatus(it)
            }
        })
        vm.addFilterAction.observe(this, Observer {
            it?.handleEvent()?.let {
                Timber.d("add filter $it")
                findNavController().popBackStack()
            }
        })
    }
    
    private fun setupActionsFromView() {
        binding.readMoreButton.setOnClickListener {
            customTabHelper.openCustomTab(vm.currentUrl)
        }
        binding.favoriteButton.setOnClickListener {
            vm.favoriteFeed()
        }
        binding.addFilterButton.setOnClickListener {
            showFilterCandidate()
        }
        binding.archiveButton.setOnClickListener {
            vm.archiveFeed()
            findNavController().popBackStack()
        }
        binding.commentButton.setOnClickListener {
            CommentBottomSheetDialog().show(fragmentManager, "comments")
        }
    }
    
    private fun showFilterCandidate() {
        PickFilterDialogFragment().show(fragmentManager, vm.currentUrl)
    }
}
