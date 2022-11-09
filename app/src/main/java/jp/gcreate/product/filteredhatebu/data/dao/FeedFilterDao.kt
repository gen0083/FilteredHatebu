package jp.gcreate.product.filteredhatebu.data.dao

import androidx.room.*
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedFilterDao {
    @Query("select * from feed_filter order by createdAt desc")
    suspend fun getAllFilters(): List<FeedFilter>

    @Query("select * from feed_filter order by createdAt desc")
    fun subscribeAllFilteres(): Flow<List<FeedFilter>>

    @Query("select * from feed_filter order by createdAt desc, id desc limit 1")
    suspend fun getNewestFilter(): FeedFilter?

    @Query("select * from feed_filter where filter=:filter limit 1")
    suspend fun getFilter(filter: String): FeedFilter?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilter(vararg filter: FeedFilter): Array<Long>

    @Delete
    suspend fun deleteFilter(filter: FeedFilter)

    @Query("delete from feed_filter where filter=:filter")
    suspend fun deleteFilter(filter: String)

    @Query("update feed_filter set filter=:newFilter where filter=:oldFilter")
    suspend fun updateFilter(oldFilter: String, newFilter: String)
}
