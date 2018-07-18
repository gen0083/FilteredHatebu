package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class PagingFeedListAdapter @Inject constructor(
    private val faviconUtil: FaviconUtil
) : PagedListAdapter<FeedData, FeedListViewHolder>(
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
        return FeedListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed_list_item, parent, false),
            faviconUtil)
    }
    
    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            holder.itemView.rootView.setOnClickListener {
                clickEventSender.value = HandleOnceEvent(item)
            }
        } else {
            Timber.d("item = null on PagingFeedListAdapter position:$position (holder:$holder)")
        }
    }
}