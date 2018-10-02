package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.ItemEntryBookmarksBinding
import jp.gcreate.product.filteredhatebu.model.HatebuBookmark
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder
import jp.gcreate.product.filteredhatebu.ui.common.UrlSpanFactory

class FeedCommentsAdapter(
    private val urlSpanFactory: UrlSpanFactory
) : ListAdapter<HatebuBookmark, DataBindingViewHolder<ItemEntryBookmarksBinding>>(
    object : DiffUtil.ItemCallback<HatebuBookmark>() {
        override fun areItemsTheSame(oldItem: HatebuBookmark, newItem: HatebuBookmark): Boolean {
            return oldItem.user == newItem.user
        }
    
        override fun areContentsTheSame(oldItem: HatebuBookmark,
                                        newItem: HatebuBookmark): Boolean {
            return oldItem == newItem
        }
    }
){
    
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DataBindingViewHolder<ItemEntryBookmarksBinding> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry_bookmarks, parent, false)
        // comments内にurlが含まれている場合にクリック可能にする
        view.findViewById<TextView>(R.id.comment).apply {
            setSpannableFactory(urlSpanFactory)
            movementMethod = LinkMovementMethod.getInstance()
        }
        return DataBindingViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: DataBindingViewHolder<ItemEntryBookmarksBinding>,
                                  position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            bookmark = item
            executePendingBindings()
        }
    }
}
