package jp.gcreate.product.filteredhatebu.data.entities

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
    val count: Int = 0,
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false
)
