package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.databinding.FragmentFilterListBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.presentation.feedlist.FeedListAdapter
import javax.inject.Inject

class FilterListFragment : DaggerFragment() {
    private lateinit var binding: FragmentFilterListBinding
    private lateinit var vm: FilterListViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var feedListAdapter: FeedListAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFilterListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProviders.of(activity!!, factory)[FilterListViewModel::class.java]
        
        setupRecyclerView()
        setupActionFromView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = feedListAdapter
        }
    }
    
    private fun setupActionFromView() {
    }
    
    private fun subscribeViewModel() {
    }
}