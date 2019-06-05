package jp.gcreate.product.filteredhatebu.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_feed")
data class DeletedFeed(
    @PrimaryKey(autoGenerate = false)
    val url: String
)
