package jp.gcreate.product.filteredhatebu.presentation.archive

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentArchivedFeedBinding
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListAdapter
import jp.gcreate.product.filteredhatebu.ui.common.SwipeDismissCallback
import timber.log.Timber
import javax.inject.Inject

class ArchivedFeedFragment : DaggerFragment() {
    private lateinit var binding: FragmentArchivedFeedBinding
    @Inject lateinit var vm: ArchivedFeedViewModel
    @Inject lateinit var feedListAdapter: FeedListAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentArchivedFeedBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        setupActionsFromView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            adapter = feedListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
        ItemTouchHelper(SwipeDismissCallback(activity!!, R.drawable.ic_delete) { adapterPosition ->
            Timber.d("swiped $adapterPosition")
        }).attachToRecyclerView(binding.recyclerView)
        feedListAdapter.clickEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                val dest = ArchivedFeedFragmentDirections
                    .action_navigation_feed_list_to_archiveFeedFragment(it.url)
                findNavController().navigate(dest)
            }
        })
    }
    
    private fun setupActionsFromView() {
    }
    
    private fun subscribeViewModel() {
        vm.archivedFeeds.observe(this, Observer {
            it?.let {
                feedListAdapter.submitList(it)
                binding.noContentGroup.isVisible = it.isEmpty()
            }
        })
    }
}