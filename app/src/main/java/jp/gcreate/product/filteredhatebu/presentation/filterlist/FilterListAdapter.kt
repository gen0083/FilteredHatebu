package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeedInfo
import jp.gcreate.product.filteredhatebu.databinding.ItemFilteredFeedInfoBinding
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder
import javax.inject.Inject

private typealias VH = DataBindingViewHolder<ItemFilteredFeedInfoBinding>

@FragmentScope
class FilterListAdapter @Inject constructor(
) : ListAdapter<FilteredFeedInfo, VH>(
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
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        TODO(
            "not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.binding.info = item
    }
}
