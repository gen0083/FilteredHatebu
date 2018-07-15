package jp.gcreate.product.filteredhatebu.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeedInfo

@Dao
interface FilteredFeedDao {
    
    /**
     * フィルタとそのフィルタに引っかかっている記事の件数を取得する
     */
    @Query("select feed_filter.filter, count(filteredUrl) as feedCount from filtered_feed" +
           " inner join feed_filter on filtered_feed.filteredId=feed_filter.id group by filteredId")
    fun getFilteredInformation(): List<FilteredFeedInfo>
    
    @Insert
    fun insertFilteredFeed(vararg filtered: FilteredFeed)
    
    @Query("select * from filtered_feed where filteredId=:filterId")
    fun getFilteredFeed(filterId: Long): List<FilteredFeed>
}