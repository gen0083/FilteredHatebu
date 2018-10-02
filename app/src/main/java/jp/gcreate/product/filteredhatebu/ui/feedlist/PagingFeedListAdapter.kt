package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import jp.gcreate.product.filteredhatebu.ui.common.StickyHeaderDecoration
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

class PagingFeedListAdapter(
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
), StickyHeaderDecoration.StickyHeaderInterface {
    
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
            holder.clear()
        }
    }
    
    override fun getGroupHeaderText(position: Int): String {
        return when {
            position < 0         -> ""
            position > itemCount -> ""
            else                 -> getItem(position)?.fetchedAt?.format(
                DateTimeFormatter.ofPattern("YYYY-MM-dd").withZone(ZoneId.systemDefault())
            ) ?: ""
        }
    }
    
    override fun isBoundary(position: Int): Boolean {
        // swipeしたアイテムのpositionが-1でここに来る場合がある
        if (position < 0) return false
        if (position == 0) return true
        if (position > itemCount) return false
        return getGroupHeaderText(position) != getGroupHeaderText(position - 1)
    }
}
