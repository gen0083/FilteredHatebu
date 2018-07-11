package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.CustomApplication
import jp.gcreate.product.filteredhatebu.databinding.FragmentFeedDetailBinding
import jp.gcreate.product.filteredhatebu.model.HatebuComments
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
        vm.hatebuComments.observe(this, Observer {
            if (it == null) return@Observer
            when(it) {
                is HatebuComments.Error -> showSnackbar("error")
                is HatebuComments.Disallow -> showSnackbar("comments disallow")
                is HatebuComments.Empty -> showSnackbar("comments empty")
                is HatebuComments.Comments -> showSnackbar("comments ${it.comments.size}")
            }
        })
        val url = FeedDetailFragmentArgs.fromBundle(arguments).feedUrl
        vm.fetchFeed(url)
    }
    
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}