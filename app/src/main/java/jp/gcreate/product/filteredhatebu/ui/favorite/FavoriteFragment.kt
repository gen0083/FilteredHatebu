package jp.gcreate.product.filteredhatebu.ui.favorite

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import jp.gcreate.product.filteredhatebu.databinding.FragmentFavoriteBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListAdapter
import javax.inject.Inject

class FavoriteFragment : DaggerFragment() {
    
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var vm: FavoriteViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var feedAdapter: FeedListAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(factory)
        
        setupRecyclerView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
        feedAdapter.clickEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                val dest = FavoriteFragmentDirections.actionNavigationFavoriteToFeedDetail(it.url)
                findNavController().navigate(dest)
            }
        })
    }
    
    private fun subscribeViewModel() {
        vm.favoriteFeed.observe(this, Observer {
            val isEmpty = it?.isEmpty() ?: true
            binding.noContentGroup.isVisible = isEmpty
            binding.recyclerView.isGone = isEmpty
            
            it?.let { feedAdapter.submitList(it) }
        })
    }
}