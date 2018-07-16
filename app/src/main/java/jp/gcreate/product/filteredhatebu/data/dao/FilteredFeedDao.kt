package jp.gcreate.product.filteredhatebu.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
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
    
    @Query("select feed_filter.filter, count(filteredUrl) as feedCount from filtered_feed" +
           " inner join feed_filter on filtered_feed.filteredId=feed_filter.id group by filteredId")
    fun subscribeFilteredInformation(): LiveData<List<FilteredFeedInfo>>
    
    @Insert
    fun insertFilteredFeed(vararg filtered: FilteredFeed)
    
    @Query(
        "select * from filtered_feed left join (select * from feed_filter) on filteredId=id where filter=:filter")
    fun getFilteredFeed(filter: String): List<FilteredFeed>
    
    @Query(
        "select * from feed_data left join (select * from filtered_feed left join (select * from feed_filter) on filteredId=id) on feed_data.url=filteredUrl where filter=:filter")
    fun getFilteredFeeds(filter: String): List<FeedData>
}