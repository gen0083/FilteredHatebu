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
    
    @Query("select * from feed_filter order by createdAt desc, id desc limit 1")
    fun getNewestFilter(): FeedFilter?
    
    @Query("select * from feed_filter where filter=:filter limit 1")
    fun getFilter(filter: String): FeedFilter?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFilter(vararg filter: FeedFilter): Array<Long>
    
    @Delete
    fun deleteFilter(filter: FeedFilter)
    
    @Query("delete from feed_filter where filter=:filter")
    fun deleteFilter(filter: String)
    
    @Query("update feed_filter set filter=:newFilter where filter=:oldFilter")
    fun updateFilter(oldFilter: String, newFilter: String)
}
