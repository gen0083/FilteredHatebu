package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.DialogListFilterStateBinding
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ext.injectViewModel
import timber.log.Timber
import javax.inject.Inject

class ListFilterStateDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogListFilterStateBinding
    private lateinit var vm: FeedListViewModel
    @Inject lateinit var factory: ViewModelProviderFactory
    
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DialogListFilterStateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = injectViewModel(factory)
        val checkedId = when (vm.filterState) {
            FeedListViewModel.FilterState.NEW_FEEDS     -> R.id.filter_new_feed
            FeedListViewModel.FilterState.ARCHIVE_FEEDS -> R.id.filter_archive
        }
        binding.filterGroup.check(checkedId)
        
        binding.closeButton.setOnClickListener {
            Timber.d("close button clicked")
            dismiss()
        }
        binding.filterGroup.setOnCheckedChangeListener { group, id ->
            Timber.d("checked change group:$group id:$id")
            when (id) {
                R.id.filter_new_feed -> vm.showNewFeeds()
                R.id.filter_archive  -> vm.showArchiveFeeds()
                else                 -> Timber.d("unknown id")
            }
        }
    }
}
