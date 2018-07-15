package jp.gcreate.product.filteredhatebu.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "deleted_feed")
data class DeletedFeed(
    @PrimaryKey(autoGenerate = false)
    val url: String
)
