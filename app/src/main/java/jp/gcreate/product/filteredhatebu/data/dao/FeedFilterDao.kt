package jp.gcreate.product.filteredhatebu.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter

@Dao
interface FeedFilterDao {
    @Query("select * from feed_filter order by createdAt desc")
    fun getAllFilters(): List<FeedFilter>
    
    @Query("select * from feed_filter order by createdAt desc")
    fun subscribeAllFilteres(): LiveData<List<FeedFilter>>
    
    @Query("select * from feed_filter order by createdAt desc limit 1")
    fun getNewestFilter(): FeedFilter
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFilter(vararg filter: FeedFilter)
    
    @Delete
    fun deleteFilter(filter: FeedFilter)
}
