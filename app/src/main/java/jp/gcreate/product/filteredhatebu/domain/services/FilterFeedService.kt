package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

class FilterFeedService(private val appRoomDatabase: AppRoomDatabase) {

    private val feedFilterDao = appRoomDatabase.feedFilterDao()
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val filteredFeedDao = appRoomDatabase.filteredFeedDao()
    private var deleteCommand: DeleteCommand? = null
    private var addedFilter: String? = null
    val filteredFeeds: Flow<List<FeedData>> = feedDataDao.subscribeFilteredNewFeeds()
    private val addFilterEventEmitter = MutableStateFlow<HandleOnceEvent<FeedFilter>?>(null)
    val addFilterEvent: StateFlow<HandleOnceEvent<FeedFilter>?> = addFilterEventEmitter
    private val deleteFilterEventEmitter = MutableStateFlow<HandleOnceEvent<FeedFilter>?>(null)
    val deleteFilterEvent: StateFlow<HandleOnceEvent<FeedFilter>?> = deleteFilterEventEmitter
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun addNewFeed(feedData: FeedData) = scope.async {
        val result = feedDataDao.insertFeed(feedData)
        if (result[0] == -1L) {
            feedDataDao.updateHatebuCount(feedData.url, feedData.count)
            return@async false
        }
        // filterのマッチング処理
        val filters = feedFilterDao.getAllFilters()
        filters.forEach { (id, filter, _) ->
            if (feedData.url.contains(filterToRegex(filter))) {
                appRoomDatabase.filteredFeedDao().insertFilteredFeed(FilteredFeed(id, feedData.url))
            }
        }
        return@async true
    }.await()

    fun addFilter(filter: String) = scope.launch {
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
        addFilterEventEmitter.emit(HandleOnceEvent(insert))
    }

    fun undoAdd() = scope.launch(Dispatchers.Default) {
        addedFilter?.let {
            feedFilterDao.deleteFilter(it)
            addedFilter = null
        }
    }

    fun deleteFilter(filter: String) = scope.launch(Dispatchers.Default) {
        val target = feedFilterDao.getFilter(filter)
        if (target == null) {
            Timber.e("$filter is not exist")
            return@launch
        }
        val feeds = filteredFeedDao.getFilteredFeed(target.filter)
        deleteCommand = DeleteCommand(target, feeds)
        feedFilterDao.deleteFilter(filter)
        deleteFilterEventEmitter.emit(HandleOnceEvent(target))
    }

    fun undoDelete() = scope.launch {
        deleteCommand?.let {
            feedFilterDao.insertFilter(it.filter)
            if (it.filteredFeeds.isNotEmpty()) {
                filteredFeedDao.insertFilteredFeed(*it.filteredFeeds.toTypedArray())
            }
            deleteCommand = null
        }
    }

    private fun filterToRegex(filter: String): Regex = "https?://[^/]*$filter".toRegex()
}

data class DeleteCommand(
    val filter: FeedFilter,
    val filteredFeeds: List<FilteredFeed>
)
