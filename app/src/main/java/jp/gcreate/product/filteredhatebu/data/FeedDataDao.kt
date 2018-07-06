package jp.gcreate.product.filteredhatebu.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface FeedDataDao {
    
    @Query("select * from feed_data order by pubDate desc")
    fun getAllFeeds(): List<FeedData>
    
    @Query("select * from feed_data where (isRead=0 AND isArchived=0 AND isFavorite=0)" +
           " order by pubDate Desc")
    fun getNewFeeds(): List<FeedData>
    
    @Query("select * from feed_data where (isRead=0 and isArchived=0 and isFavorite=0)" +
           " order by pubDate desc")
    fun subscribeNewFeeds(): LiveData<List<FeedData>>
    
    @Query("select * from feed_data where (isArchived=1) order by pubDate DESC")
    fun getArchivedFeeds(): List<FeedData>
    
    @Query("select * from feed_data where isArchived=1 order by pubDate desc")
    fun subscribeArchivedFeeds(): LiveData<List<FeedData>>
    
    @Query("select * from feed_data where isFavorite=1 order by pubDate desc")
    fun getFavoriteFeeds(): List<FeedData>
    
    @Query("select * from feed_data where isFavorite=1 order by pubDate desc")
    fun subscribeFavoriteFeeds(): LiveData<List<FeedData>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFeed(vararg feed: FeedData)
    
    @Query("update feed_data set isRead=1 where url = :url")
    fun updateStatusRead(url: String)
    
    @Query("update feed_data set isArchived=1 where url = :url")
    fun updateStatusArchived(url: String)
    
    @Query("update feed_data set isFavorite=1 where url=:url")
    fun updateStatusFavorite(url: String)
    
    @Delete
    fun deleteFeed(feed: FeedData)
}