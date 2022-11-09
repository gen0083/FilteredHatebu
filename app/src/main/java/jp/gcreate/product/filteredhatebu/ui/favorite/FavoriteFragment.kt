package jp.gcreate.product.filteredhatebu.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.gcreate.product.filteredhatebu.databinding.FragmentFavoriteBinding
import jp.gcreate.product.filteredhatebu.ui.feedlist.FeedListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val vm: FavoriteViewModel by activityViewModel()
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
        feedAdapter.clickEvent.observe(viewLifecycleOwner, Observer {
            it?.handleEvent()?.let {
                val dest = FavoriteFragmentDirections.actionNavigationFavoriteToFeedDetail(it.url)
                findNavController().navigate(dest)
            }
        })
    }
    
    private fun subscribeViewModel() {
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    vm.favoriteFeed.collectLatest { list ->
                        val isEmpty = list.isEmpty()
                        binding.noContentGroup.isVisible = isEmpty
                        binding.recyclerView.isGone = isEmpty

                        feedAdapter.submitList(list)
                    }
                }
            }
        }
    }
}
