package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeedInfo
import jp.gcreate.product.filteredhatebu.databinding.ItemFilteredFeedInfoBinding
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import javax.inject.Inject

private typealias VH = DataBindingViewHolder<ItemFilteredFeedInfoBinding>

@FragmentScope
class FilterListAdapter @Inject constructor() : ListAdapter<FilteredFeedInfo, VH>(
    object : DiffUtil.ItemCallback<FilteredFeedInfo>() {
        override fun areItemsTheSame(oldItem: FilteredFeedInfo,
                                     newItem: FilteredFeedInfo): Boolean {
            return oldItem.filter == newItem.filter
        }
        
        override fun areContentsTheSame(oldItem: FilteredFeedInfo,
                                        newItem: FilteredFeedInfo): Boolean {
            return oldItem == newItem
        }
    }
) {
    
    private val clickEventEmitter = MutableLiveData<HandleOnceEvent<FilteredFeedInfo>>()
    val clickEvent: LiveData<HandleOnceEvent<FilteredFeedInfo>> = clickEventEmitter
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context)
                      .inflate(R.layout.item_filtered_feed_info, parent, false))
    }
    
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.binding.info = item
        holder.binding.root.setOnClickListener(
            if (item.feedCount == 0) {
                null
            } else {
                View.OnClickListener { clickEventEmitter.value = HandleOnceEvent(item) }
            }
        )
    }
}
