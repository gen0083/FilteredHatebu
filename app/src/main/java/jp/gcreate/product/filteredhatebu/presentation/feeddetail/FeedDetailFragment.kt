package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.databinding.ActivityHatebuFeedDetailBinding
import javax.inject.Inject

class FeedDetailFragment : Fragment() {
    private lateinit var binding: ActivityHatebuFeedDetailBinding
    @Inject lateinit var vm: FeedDetailViewModel
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = ActivityHatebuFeedDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}