package jp.gcreate.product.filteredhatebu.presentation.feedlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.databinding.ItemFeedListItemBinding
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import javax.inject.Inject

@ActivityScope
class FeedListAdapter @Inject constructor() : ListAdapter<FeedData, FeedListViewHolder>(
    object : DiffUtil.ItemCallback<FeedData>() {
        override fun areItemsTheSame(oldItem: FeedData, newItem: FeedData): Boolean {
            return oldItem.url == newItem.url
        }
        
        override fun areContentsTheSame(oldItem: FeedData, newItem: FeedData): Boolean {
            return oldItem == newItem
        }
    }
) {
    private val clickEventSender: MutableLiveData<HandleOnceEvent<FeedData>> = MutableLiveData()
    val clickEvent: LiveData<HandleOnceEvent<FeedData>> = clickEventSender
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedListViewHolder {
        return FeedListViewHolder(LayoutInflater.from(parent.context)
                                      .inflate(R.layout.item_feed_list_item, parent, false))
    }
    
    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.rootView.setOnClickListener {
            clickEventSender.value = HandleOnceEvent(item)
        }
    }
}

class FeedListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private val binding: ItemFeedListItemBinding = ItemFeedListItemBinding.bind(itemView)
    
    fun bind(feedData: FeedData) {
        binding.item = feedData
    }
}