package jp.gcreate.product.filteredhatebu.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "filtered_feed",
    primaryKeys = ["filteredId", "filteredUrl"],
    foreignKeys = [
        (ForeignKey(
            entity = FeedFilter::class, parentColumns = ["id"],
            childColumns = ["filteredId"], onDelete = ForeignKey.CASCADE
        )),
        (ForeignKey(
            entity = FeedData::class, parentColumns = ["url"],
            childColumns = ["filteredUrl"], onDelete = ForeignKey.CASCADE
        ))
    ],
    indices = [Index(value = ["filteredUrl"])],
)
data class FilteredFeed(
    val filteredId: Long,
    val filteredUrl: String
)

data class FilteredFeedInfo(
    val filter: String,
    val feedCount: Int
)
