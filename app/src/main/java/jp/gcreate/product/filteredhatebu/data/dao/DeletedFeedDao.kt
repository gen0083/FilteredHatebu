package jp.gcreate.product.filteredhatebu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.DeletedFeed

@Dao
interface DeletedFeedDao {
    @Query("select exists (select * from deleted_feed where url=:url)")
    fun isDeletedUrl(url: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDeletedUrl(deletedFeed: DeletedFeed)
}
