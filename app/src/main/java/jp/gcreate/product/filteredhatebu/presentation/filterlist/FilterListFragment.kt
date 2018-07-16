package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFilterListBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import timber.log.Timber
import javax.inject.Inject

class FilterListFragment : DaggerFragment() {
    private lateinit var binding: FragmentFilterListBinding
    private lateinit var vm: FilterListViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var filterListAdapter: FilterListAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFilterListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(factory)
        
        setupRecyclerView()
        setupActionFromView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = filterListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        filterListAdapter.clickEvent.observe(this, Observer {
            Timber.d("filter list clicked: $it")
            it?.handleEvent()?.let {
                val dest = FilterListFragmentDirections
                    .ActionNavigationFilterToFilterDetailFragment(it.filter, it.feedCount)
                findNavController().navigate(dest)
            }
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
        vm.deleteFilterEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                Snackbar.make(binding.root, R.string.delete_filter, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel, { vm.undoDeleteFilter() })
                    .show()
            }
        })
    }
}