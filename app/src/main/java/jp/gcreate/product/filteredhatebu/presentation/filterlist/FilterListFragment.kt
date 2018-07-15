package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.databinding.FragmentFilterListBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import timber.log.Timber
import javax.inject.Inject

class FilterListFragment : DaggerFragment() {
    private lateinit var binding: FragmentFilterListBinding
    private lateinit var vm: FilterListViewModel
    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var filterListAdapter: FilterListAdapter
    
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
            adapter = filterListAdapter
            layoutManager = linearLayoutManager
        }
        filterListAdapter.clickEvent.observe(this, Observer {
            Timber.d("filter list clicked: $it")
        })
    }
    
    private fun setupActionFromView() {
    }
    
    private fun subscribeViewModel() {
        vm.filterInfo.observe(this, Observer { list ->
            list?.let {
                filterListAdapter.submitList(it)
                binding.noContentGroup.isVisible = it.isEmpty()
            }
        })
    }
}