package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.databinding.DialogListFilterStateBinding
import timber.log.Timber

class ListFilterStateDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogListFilterStateBinding
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DialogListFilterStateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    
        parseResult(
            arguments).run {
            binding.checkShowNewFeed.isChecked = first
            binding.checkShowArchiveFeed.isChecked = second
            binding.checkShowFavoriteFeed.isChecked = third
        }
        
        binding.closeButton.setOnClickListener {
            Timber.d("close button clicked")
            dismiss()
        }
        binding.checkShowNewFeed.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("new box: $isChecked")
        }
        binding.checkShowArchiveFeed.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("archive box: $isChecked")
        }
        binding.checkShowFavoriteFeed.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("favorite box: $isChecked")
        }
    }
    
    companion object {
        const val REQUEST_CODE = 10
        const val KEY_ARCHIVED_FEED = "archived_feed"
        const val KEY_FAVORITE_FEED = "favorite_feed"
        const val KEY_NEW_FEED = "new_feed"
        
        fun parseResult(args: Bundle?): Triple<Boolean, Boolean, Boolean> {
            val isShowNewFeed = args?.getBoolean(
                KEY_NEW_FEED) ?: true
            val isShowArchive = args?.getBoolean(
                KEY_ARCHIVED_FEED) ?: false
            val isShowFavorite = args?.getBoolean(
                KEY_FAVORITE_FEED) ?: false
            return Triple(isShowNewFeed, isShowArchive, isShowFavorite)
        }
    }
}

fun Fragment.createFilterDialog(showNewFeed: Boolean = true, showArchivedFeed: Boolean = false,
                                showFavoriteFeed: Boolean = false): ListFilterStateDialog {
    return ListFilterStateDialog().apply {
        arguments = Bundle().apply {
            putBoolean(
                ListFilterStateDialog.KEY_NEW_FEED, showNewFeed)
            putBoolean(
                ListFilterStateDialog.KEY_ARCHIVED_FEED, showArchivedFeed)
            putBoolean(
                ListFilterStateDialog.KEY_FAVORITE_FEED, showFavoriteFeed)
        }
        setTargetFragment(this@createFilterDialog,
                          ListFilterStateDialog.REQUEST_CODE)
    }
}