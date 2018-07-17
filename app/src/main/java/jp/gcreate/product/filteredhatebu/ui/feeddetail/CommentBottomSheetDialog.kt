package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import jp.gcreate.product.filteredhatebu.databinding.DialogCommentBottomSheetBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import timber.log.Timber
import javax.inject.Inject

class CommentBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogCommentBottomSheetBinding
    private lateinit var vm: FeedDetailViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    @Inject lateinit var commentsAdapter: FeedCommentsAdapter
    
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DialogCommentBottomSheetBinding.inflate(inflater, container, false)
        binding.closeButton.setOnClickListener { dismiss() }
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(factory)
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