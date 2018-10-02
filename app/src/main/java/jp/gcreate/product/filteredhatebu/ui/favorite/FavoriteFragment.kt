package jp.gcreate.product.filteredhatebu.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.gcreate.product.filteredhatebu.databinding.FragmentFavoriteBinding
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FavoriteFragment : Fragment() {
    
    private lateinit var binding: FragmentFavoriteBinding
    private val vm: FavoriteViewModel by sharedViewModel()
    private val feedAdapter: FeedListAdapter by inject()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        setupRecyclerView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
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
