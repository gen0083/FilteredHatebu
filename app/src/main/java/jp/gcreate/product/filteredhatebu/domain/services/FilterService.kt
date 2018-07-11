package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

@AppScope
class FilterService @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) {
    
    private val feedFilterDao = appRoomDatabase.feedFilterDao()
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val filteredFeedDao = appRoomDatabase.filteredFeedDao()
    
    fun addFilter(filter: String) = launch(CommonPool) {
        val ids = feedFilterDao.insertFilter(FeedFilter(0, filter, ZonedDateTime.now()))
        val id = ids[0]
        
        if (id == 0L) {
            Timber.d("filter:$filter is already exist")
            return@launch
        }
        
        feedDataDao.getAllFeeds().forEach {
            if (it.isMatchFilter(filter)) {
                async { filteredFeedDao.insertFilteredFeed(FilteredFeed(id, it.url)) }
            }
        }
    }
    
    fun deleteFilter(filter: String) {
        launch {
            feedFilterDao.deleteFilter(filter)
        }
    }
}
