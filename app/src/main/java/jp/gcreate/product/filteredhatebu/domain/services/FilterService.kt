package jp.gcreate.product.filteredhatebu.domain.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

class FilterService(private val appRoomDatabase: AppRoomDatabase) {
    
    private val feedFilterDao = appRoomDatabase.feedFilterDao()
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val filteredFeedDao = appRoomDatabase.filteredFeedDao()
    private var deleteCommand: DeleteCommand? = null
    private var addedFilter: String? = null
    private val addFilterEventEmitter = MutableLiveData<HandleOnceEvent<FeedFilter>>()
    val addFilterEvent: LiveData<HandleOnceEvent<FeedFilter>> = addFilterEventEmitter
    private val deleteFilterEventEmitter = MutableLiveData<HandleOnceEvent<FeedFilter>>()
    val deleteFilterEvent: LiveData<HandleOnceEvent<FeedFilter>> = deleteFilterEventEmitter
    
    fun addFilter(filter: String) = GlobalScope.launch(Dispatchers.Default) {
        addedFilter = filter
        val insert = FeedFilter(0, filter, ZonedDateTime.now())
        val ids = feedFilterDao.insertFilter(insert)
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
        addFilterEventEmitter.postValue(HandleOnceEvent(insert))
    }
    
    fun undoAdd() = GlobalScope.launch(Dispatchers.Default) {
        addedFilter?.let {
            feedFilterDao.deleteFilter(it)
            addedFilter = null
        }
    }
    
    fun deleteFilter(filter: String) = GlobalScope.launch(Dispatchers.Default) {
        val target = feedFilterDao.getFilter(filter)
        if (target == null) {
            Timber.e("$filter is not exist")
            return@launch
        }
        val feeds = filteredFeedDao.getFilteredFeed(target.filter)
        deleteCommand = DeleteCommand(target, feeds)
        feedFilterDao.deleteFilter(filter)
        deleteFilterEventEmitter.postValue(HandleOnceEvent(target))
    }
    
    fun undoDelete() = GlobalScope.launch(Dispatchers.Default) {
        deleteCommand?.let {
            feedFilterDao.insertFilter(it.filter)
            if (it.filteredFeeds.isNotEmpty()) {
                filteredFeedDao.insertFilteredFeed(*it.filteredFeeds.toTypedArray())
            }
            deleteCommand = null
        }
    }
}

data class DeleteCommand(
    val filter: FeedFilter,
    val filteredFeeds: List<FilteredFeed>
)
