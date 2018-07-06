package jp.gcreate.product.filteredhatebu.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "feed_data")
data class FeedData(
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val title: String,
    val summary: String,
    val pubDate: ZonedDateTime,
    val isRead: Boolean = false,
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false
)
