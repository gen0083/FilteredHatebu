package jp.gcreate.product.filteredhatebu.presentation.feedlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedListBinding

class FeedListFragment : Fragment() {
    private lateinit var binding: FragmentFeedListBinding
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.noContentGroup.isVisible = binding.recyclerView.isEmpty()
    }
}