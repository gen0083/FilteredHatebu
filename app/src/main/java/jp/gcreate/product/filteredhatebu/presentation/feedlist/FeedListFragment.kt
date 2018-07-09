package jp.gcreate.product.filteredhatebu.presentation.feedlist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.State
import androidx.work.WorkManager
import jp.gcreate.product.filteredhatebu.CustomApplication
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedListBinding
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import timber.log.Timber
import javax.inject.Inject

class FeedListFragment : Fragment() {
    @Inject lateinit var vm: FeedListViewModel
    @Inject lateinit var feedListAdapter: FeedListAdapter
    private lateinit var binding: FragmentFeedListBinding
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CustomApplication.getActivityComponent(activity)
            .inject(this)
        setUpRecyclerView()
        binding.swipeRefresh.setOnRefreshListener {
            Timber.d("swipe refresh")
            fetchFeeds()
        }
        binding.noContentReloadButton.setOnClickListener {
            binding.swipeRefresh.isRefreshing = true
            fetchFeeds()
        }
        vm.newFeeds.observe(this, Observer { list ->
            Timber.d("update list size=${list?.size}")
            list?.let {
                feedListAdapter.submitList(it)
                binding.noContentGroup.isVisible = it.isEmpty()
            }
        })
        feedListAdapter.clickEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                val direction = FeedListFragmentDirections.Action_navigation_feed_list_to_feedDetailFragment(it.url)
                findNavController().navigate(direction)
            }
        })
    }
    
    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            adapter = feedListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
    }
    
    private fun fetchFeeds() {
        val work = OneTimeWorkRequestBuilder<CrawlFeedsWork>().build()
        WorkManager.getInstance()?.let {
            it.beginUniqueWork("fetch_new_feeds", ExistingWorkPolicy.REPLACE, work)
                .enqueue()
            it.getStatusById(work.id)
                .observe(this, Observer {
                    Timber.d("status updated $it")
                    if (it?.state in arrayOf(State.SUCCEEDED, State.FAILED)) {
                        val count = it?.outputData?.getInt(CrawlFeedsWork.KEY_NEW_FEEDS_COUNT, 0)
                        Snackbar.make(binding.root, "$count feeds new comes!", Snackbar.LENGTH_SHORT)
                            .show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                })
        }
    }
}