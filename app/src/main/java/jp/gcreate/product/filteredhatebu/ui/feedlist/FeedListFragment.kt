package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedListBinding
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.ui.common.StickyHeaderDecoration
import jp.gcreate.product.filteredhatebu.ui.common.SwipeDismissCallback
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class FeedListFragment : Fragment() {
    private val vm: FeedListViewModel by sharedViewModel()
    private val feedListAdapter: PagingFeedListAdapter by inject()
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
        
        setUpRecyclerView()
        setupActionFromView()
        subscribeViewModel()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.feed_list_menu, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_list_filter) {
            ListFilterStateDialog().show(requireFragmentManager(), "filter_dialog")
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
    
    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            addItemDecoration(StickyHeaderDecoration(requireActivity()))
            adapter = feedListAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        }
        ItemTouchHelper(SwipeDismissCallback(requireActivity()) { adapterPosition ->
            Timber.d("swiped $adapterPosition")
            vm.archiveFeedAtPosition(adapterPosition)
        }).attachToRecyclerView(binding.recyclerView)
        feedListAdapter.clickEvent.observe(viewLifecycleOwner, Observer {
            it?.handleEvent()?.let { data ->
                val direction = FeedListFragmentDirections
                    .actionNavigationFeedListToFeedDetailFragment(data.url)
                findNavController().navigate(direction)
            }
        })
    }
    
    private fun subscribeViewModel() {
        vm.newFeeds.observe(viewLifecycleOwner, Observer { list ->
            Timber.d("update list size=${list?.size}")
            list?.let {
                feedListAdapter.submitList(it)
                binding.noContentGroup.isVisible = it.isEmpty()
            }
        })
        vm.archiveMessage.observe(viewLifecycleOwner, Observer {
            it?.handleEvent()?.let {
                Snackbar.make(binding.root, R.string.archive_done, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel) { vm.undoArchive() }
                    .show()
            }
        })
        vm.addFilterEvent.observe(viewLifecycleOwner, Observer {
            it?.handleEvent()?.let {
                Snackbar.make(binding.root, R.string.add_filter_done, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel) { vm.cancelAddFilter() }
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
        WorkManager.getInstance(requireContext()).run {
            beginUniqueWork("fetch_new_feeds", ExistingWorkPolicy.REPLACE, work)
                .enqueue()
            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(work.id)
                .observe(viewLifecycleOwner, Observer {
                    Timber.d("status updated $it")
                    if (it?.state in arrayOf(WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED)) {
                        val count = it?.outputData?.getInt(CrawlFeedsWork.KEY_NEW_FEEDS_COUNT, 0)
                        Snackbar.make(
                            binding.root, "$count feeds new comes!",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                })
        }
    }
}
