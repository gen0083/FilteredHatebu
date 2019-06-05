package jp.gcreate.product.filteredhatebu.ui.filterlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeedInfo
import jp.gcreate.product.filteredhatebu.databinding.ItemFilteredFeedInfoBinding
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent

private typealias VH = DataBindingViewHolder<ItemFilteredFeedInfoBinding>

class FilterListAdapter : ListAdapter<FilteredFeedInfo, VH>(
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
        return VH(
            LayoutInflater.from(parent.context)
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
