package jp.gcreate.product.filteredhatebu.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "feed_data")
data class FeedData(
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val title: String,
    val summary: String,
    val pubDate: ZonedDateTime,
    val fetchedAt: ZonedDateTime = ZonedDateTime.now(),
    val count: Int = 0,
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false
) {
    fun isMatchFilter(filter: String): Boolean {
        val regex = "https?://[^/]*$filter".toRegex()
        return url.contains(regex)
    }
}
