package jp.gcreate.product.filteredhatebu.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import jp.gcreate.product.filteredhatebu.data.FeedData

@Entity(tableName = "filtered_feed",
        primaryKeys = ["filteredId", "filteredUrl"],
        foreignKeys = [
            (ForeignKey(entity = FeedFilter::class, parentColumns = ["id"],
                       childColumns = ["filteredId"], onDelete = ForeignKey.CASCADE)),
            (ForeignKey(entity = FeedData::class, parentColumns = ["url"],
                       childColumns = ["filteredUrl"], onDelete = ForeignKey.CASCADE))
        ])
data class FilteredFeed(
    val filteredId: Long,
    val filteredUrl: String
)

data class FilteredFeedInfo(
    val filter: String,
    val feedCount: Int
)
