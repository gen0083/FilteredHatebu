package jp.gcreate.product.filteredhatebu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
