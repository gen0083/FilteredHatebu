package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.CustomApplication
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedDetailBinding
import timber.log.Timber
import javax.inject.Inject

class FeedDetailFragment : Fragment() {
    private lateinit var binding: FragmentFeedDetailBinding
    @Inject lateinit var vm: FeedDetailViewModel
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivity) {
            CustomApplication.getActivityComponent(context)
                .inject(this)
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFeedDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.feedDetail.observe(this, Observer {
            it?.let {
                Timber.d("detail feed: $it")
                binding.item = it
            }
        })
        val url = FeedDetailFragmentArgs.fromBundle(arguments).feedUrl
        vm.fetchFeed(url)
    }
}