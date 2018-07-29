package jp.gcreate.product.filteredhatebu.ui.feedlist

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
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import jp.gcreate.product.filteredhatebu.ui.common.StickyHeaderDecoration
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class FeedListAdapter @Inject constructor(private val faviconUtil: FaviconUtil)
    : ListAdapter<FeedData, FeedListViewHolder>(
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
        holder.bind(item)
        holder.itemView.rootView.setOnClickListener {
            clickEventSender.value = HandleOnceEvent(item)
        }
    }
    
    override fun getGroupHeaderText(position: Int): String {
        return when {
            position < 0         -> ""
            position > itemCount -> ""
            else                 -> getItem(position).fetchedAt.toLocalDate().toString()
        }
    }
    
    override fun isBoundary(position: Int): Boolean {
        Timber.d("isBoundary position $position")
        // swipeしたアイテムのpositionが-1でここに来る場合がある
        if (position < 0) return false
        if (position == 0) return true
        if (position > itemCount) return false
        return getItem(position).let {
            val current = it.fetchedAt.toLocalDate()
            val prev = getItem(position - 1).fetchedAt.toLocalDate()
            return@let current != prev
        } ?: false
    }
    
}

class FeedListViewHolder(itemView: View, private val faviconUtil: FaviconUtil)
    : RecyclerView.ViewHolder(itemView) {
    
    private var job: Job? = null
    private val binding: ItemFeedListItemBinding = ItemFeedListItemBinding.bind(itemView)
    
    fun bind(feedData: FeedData) {
        job?.cancel()
        binding.item = feedData
        binding.executePendingBindings()
        job = launch(UI) {
            binding.favicon.setImageDrawable(faviconUtil.fetchFaviconWithCoroutine(feedData.url))
        }
    }
}