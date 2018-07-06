package jp.gcreate.product.filteredhatebu.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "feed_filter")
data class FeedFilter(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val filter: String,
    val createdAt: ZonedDateTime
)