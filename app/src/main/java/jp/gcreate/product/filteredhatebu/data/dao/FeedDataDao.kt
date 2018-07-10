package jp.gcreate.product.filteredhatebu.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import jp.gcreate.product.filteredhatebu.data.entities.FeedData

@Dao
interface FeedDataDao {
    
    @Query("select * from feed_data order by pubDate desc")
    fun getAllFeeds(): List<FeedData>
    
    @Query("select * from feed_data left join" +
           " (select distinct filteredUrl from filtered_feed) on url=filteredUrl" +
           " where filteredUrl is null and isArchived=0 order by pubDate desc")
    fun getFilteredNewFeeds(): List<FeedData>
    
    @Query("select * from feed_data left join" +
           " (select distinct filteredUrl from filtered_feed) on url=filteredUrl" +
           " where filteredUrl is null and isArchived=0 order by pubDate desc")
    fun subscribeFilteredNewFeeds(): LiveData<List<FeedData>>
    
    @Query("select * from feed_data where isArchived=1 order by pubDate DESC")
    fun getArchivedFeeds(): List<FeedData>
    
    @Query("select * from feed_data where isArchived=1 order by pubDate desc")
    fun subscribeArchivedFeeds(): LiveData<List<FeedData>>
    
    @Query("select * from feed_data where isFavorite=1 order by pubDate desc")
    fun getFavoriteFeeds(): List<FeedData>
    
    @Query("select * from feed_data where isFavorite=1 order by pubDate desc")
    fun subscribeFavoriteFeeds(): LiveData<List<FeedData>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFeed(vararg feed: FeedData): Array<Long>
    
    @Query("update feed_data set count=:count where url=:url")
    fun updateHatebuCount(url: String, count: Int)
    
    @Query("update feed_data set isArchived=:isArchived where url = :url")
    fun updateStatusArchived(url: String, isArchived: Boolean)
    
    @Query("update feed_data set isFavorite=:isFavorite where url=:url")
    fun updateStatusFavorite(url: String, isFavorite: Boolean)
    
    @Delete
    fun deleteFeed(feed: FeedData)
    
    @Query("select * from feed_data where url=:url limit 1")
    fun getFeed(url: String): FeedData?
}