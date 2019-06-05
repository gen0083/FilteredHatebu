package jp.gcreate.product.filteredhatebu.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "feed_filter", indices = [Index(value = ["filter"], unique = true)])
data class FeedFilter(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val filter: String,
    val createdAt: ZonedDateTime
)