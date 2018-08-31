package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.DialogCommentBottomSheetBinding
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.ext.android.inject
import timber.log.Timber

class CommentBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogCommentBottomSheetBinding
    private val vm: FeedDetailViewModel by sharedViewModel()
    private val commentsAdapter: FeedCommentsAdapter by inject()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val contextWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
        binding = DialogCommentBottomSheetBinding.inflate(inflater.cloneInContext(contextWrapper),
                                                          container, false)
        binding.closeButton.setOnClickListener { dismiss() }
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("vm=$vm")
        
        setupRecyclerView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
    }
    
    private fun subscribeViewModel() {
        vm.hatebuComments.observe(this, Observer {
            Timber.d("comments=$it")
            if (it is HatebuComments.Comments) {
                commentsAdapter.submitList(it.comments)
            } else {
                commentsAdapter.submitList(emptyList())
            }
        })
    }
}
