package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.State
import androidx.work.WorkManager
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedListBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import jp.gcreate.product.filteredhatebu.ui.common.SwipeDismissCallback
import timber.log.Timber
import javax.inject.Inject

class FeedListFragment : DaggerFragment() {
    private lateinit var vm: FeedListViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var feedListAdapter: FeedListAdapter
    private lateinit var binding: FragmentFeedListBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(factory)
        
        setUpRecyclerView()
        setupActionFromView()
        subscribeViewModel()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.feed_list_menu, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_list_filter) {
            createFilterDialog(true, false, false).show(fragmentManager, "filter_dialog")
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
    
    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            adapter = feedListAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
        ItemTouchHelper(SwipeDismissCallback(activity!!) { adapterPosition ->
            Timber.d("swiped $adapterPosition")
            vm.archiveFeedAtPosition(adapterPosition)
        }).attachToRecyclerView(binding.recyclerView)
        feedListAdapter.clickEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                val direction = FeedListFragmentDirections
                    .ActionNavigationFeedListToFeedDetailFragment(it.url)
                findNavController().navigate(direction)
            }
        })
    }
    
    private fun subscribeViewModel() {
        vm.newFeeds.observe(this, Observer { list ->
            Timber.d("update list size=${list?.size}")
            list?.let {
                feedListAdapter.submitList(it)
                binding.noContentGroup.isVisible = it.isEmpty()
            }
        })
        vm.archiveMessage.observe(this, Observer {
            it?.handleEvent()?.let {
                Snackbar.make(binding.root, R.string.archive_done, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel, { vm.undoArchive() })
                    .show()
            }
        })
        vm.addFilterEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                Snackbar.make(binding.root, R.string.add_filter_done, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel, { vm.cancelAddFilter() })
                    .show()
            }
        })
    }
    
    private fun setupActionFromView() {
        binding.swipeRefresh.apply {
            setOnRefreshListener { fetchFeeds() }
            setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
        }
        binding.noContentReloadButton.setOnClickListener {
            binding.swipeRefresh.isRefreshing = true
            fetchFeeds()
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
                        Snackbar.make(binding.root, "$count feeds new comes!",
                                      Snackbar.LENGTH_SHORT)
                            .show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                })
        }
    }
}