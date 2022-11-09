package jp.gcreate.product.filteredhatebu.data.dao

import androidx.paging.DataSource
import androidx.room.*
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import kotlinx.coroutines.flow.Flow

private const val QUERY_FILTERED_FEEDS_BY_STATE =
    "select * from feed_data left join (select distinct filteredUrl from filtered_feed)" +
        " on url=filteredUrl where filteredUrl is null and isArchived=:isArchived and" +
        " isFavorite=:isFavorite order by fetchedAt desc"
private const val QUERY_FILTERED_NEW_FEEDS =
    "select * from feed_data left join" +
        " (select distinct filteredUrl from filtered_feed) on url=filteredUrl" +
        " where filteredUrl is null and isArchived=0 order by fetchedAt desc"
private const val QUERY_ARCHIVED_FEEDS =
    "select * from feed_data where isArchived=1 order by fetchedAt DESC"
private const val QUERY_FAVORITE_FEEDS =
    "select * from feed_data where isFavorite=1 order by pubDate desc"

@Dao
interface FeedDataDao {

    @Query("select * from feed_data order by pubDate desc")
    suspend fun getAllFeeds(): List<FeedData>

    @Query(QUERY_FILTERED_FEEDS_BY_STATE)
    @RewriteQueriesToDropUnusedColumns
    suspend fun getFilteredFeedsByState(isArchived: Boolean, isFavorite: Boolean): List<FeedData>

    @Query(QUERY_FILTERED_FEEDS_BY_STATE)
    @RewriteQueriesToDropUnusedColumns
    fun subscribeFilteredFeedsByState(
        isArchived: Boolean,
        isFavorite: Boolean
    ): Flow<List<FeedData>>

    @Query(QUERY_FILTERED_NEW_FEEDS)
    @RewriteQueriesToDropUnusedColumns
    suspend fun getFilteredNewFeeds(): List<FeedData>

    @Query(QUERY_FILTERED_NEW_FEEDS)
    @RewriteQueriesToDropUnusedColumns
    fun subscribeFilteredNewFeeds(): Flow<List<FeedData>>

    @Query(QUERY_FILTERED_NEW_FEEDS)
    @RewriteQueriesToDropUnusedColumns
    fun subscribePagedFilteredNewFeeds(): DataSource.Factory<Int, FeedData>

    @Query(QUERY_ARCHIVED_FEEDS)
    suspend fun getArchivedFeeds(): List<FeedData>

    @Query(QUERY_ARCHIVED_FEEDS)
    fun subscribeArchivedFeeds(): Flow<List<FeedData>>
    
    @Query(QUERY_ARCHIVED_FEEDS)
    fun subscribePagedArchiveFeeds(): DataSource.Factory<Int, FeedData>

    @Query(QUERY_FAVORITE_FEEDS)
    suspend fun getFavoriteFeeds(): List<FeedData>

    @Query(QUERY_FAVORITE_FEEDS)
    fun subscribeFavoriteFeeds(): Flow<List<FeedData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeed(vararg feed: FeedData): Array<Long>

    @Query("update feed_data set count=:count where url=:url")
    suspend fun updateHatebuCount(url: String, count: Int)

    @Query("update feed_data set isArchived=:isArchived where url = :url")
    suspend fun updateStatusArchived(url: String, isArchived: Boolean)

    @Query("update feed_data set isFavorite=:isFavorite where url=:url")
    suspend fun updateStatusFavorite(url: String, isFavorite: Boolean)

    @Delete
    suspend fun deleteFeed(feed: FeedData)

    @Query("delete from feed_data where url = :url")
    suspend fun deleteFeedByUrl(url: String)

    @Query("select * from feed_data where url=:url limit 1")
    suspend fun getFeed(url: String): FeedData?
}
