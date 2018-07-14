package jp.gcreate.product.filteredhatebu.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.DeletedFeed

@Dao
interface DeletedFeedDao {
    @Query("select exists (select * from deleted_feed where url=:url)")
    fun isDeletedUrl(url: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDeletedUrl(deletedFeed: DeletedFeed)
}
