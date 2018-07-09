package jp.gcreate.product.filteredhatebu.presentation.feedlist

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.databinding.ItemFeedListItemBinding
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.SerialSubscription
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class FeedListAdapter @Inject constructor(
    private val client: HatenaClient.JsonService
) : ListAdapter<FeedData, FeedListViewHolder>(
    object : DiffUtil.ItemCallback<FeedData>() {
        override fun areItemsTheSame(oldItem: FeedData, newItem: FeedData): Boolean {
            return oldItem.url == newItem.url
        }
        
        override fun areContentsTheSame(oldItem: FeedData, newItem: FeedData): Boolean {
            return oldItem == newItem
        }
    }
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedListViewHolder {
        return FeedListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed_list_item, parent, false),
            client
        )
    }
    
    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class FeedListViewHolder(
    itemView: View,
    private val client: HatenaClient.JsonService
) : RecyclerView.ViewHolder(itemView) {
    
    private val subscription = SerialSubscription()
    private val binding: ItemFeedListItemBinding = ItemFeedListItemBinding.bind(itemView)
    
    fun bind(feedData: FeedData) {
        binding.item = feedData
        subscription.set(
            client.getEntry(feedData.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { binding.count.text = "${it.count}" },
                    { Timber.e(it) }
                )
        )
    }
}