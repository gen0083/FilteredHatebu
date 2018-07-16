package jp.gcreate.product.filteredhatebu.presentation.filterdetail

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeedInfo
import jp.gcreate.product.filteredhatebu.databinding.FragmentFilterDetailBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListAdapter
import timber.log.Timber
import javax.inject.Inject

class FilterDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentFilterDetailBinding
    private lateinit var vm: FilterDetailViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var feedListAdapter: FeedListAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFilterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(factory)
        val info = FilterDetailFragmentArgs.fromBundle(arguments).run {
            FilteredFeedInfo(filter, feedCount)
        }
        binding.info = info
        
        setupRecyclerView()
        setupActionFromView()
        subscribeViewModel()
        
        vm.fetchFeeds(info.filter)
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = feedListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        feedListAdapter.clickEvent.observe(this, Observer {
            Timber.d("$it")
            it?.handleEvent()?.let {
                Timber.d("click event $it")
                val dest = FilterDetailFragmentDirections.actionFilterDetailToFeedDetail(it.url)
                findNavController().navigate(dest)
            }
        })
    }
    
    private fun setupActionFromView() {
        binding.filterDelete.setOnClickListener {
            Timber.d("delete clicked")
            vm.deleteFilter()
            findNavController().popBackStack()
        }
    }
    
    private fun subscribeViewModel() {
        vm.filteredFeedList.observe(this, Observer {
            it?.let {
                feedListAdapter.submitList(it)
            }
        })
    }
}