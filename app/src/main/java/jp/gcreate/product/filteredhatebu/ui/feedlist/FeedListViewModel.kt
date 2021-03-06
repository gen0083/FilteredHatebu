package jp.gcreate.product.filteredhatebu.ui.feedlist

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import timber.log.Timber

class FeedListViewModel(
    private val appRoomDatabase: AppRoomDatabase,
    private val filterService: FilterService,
    private val archiveService: ArchiveFeedService
) : ViewModel() {
    
    private val newFeedLiveData = appRoomDatabase.feedDataDao().subscribePagedFilteredNewFeeds()
    private val archiveFeedLiveData = appRoomDatabase.feedDataDao().subscribePagedArchiveFeeds()
    val filterStateLiveData: LiveData<FilterState> = MutableLiveData()
    val newFeeds: LiveData<PagedList<FeedData>> = Transformations
        .switchMap(filterStateLiveData, Function {
            val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setPrefetchDistance(20)
                .setInitialLoadSizeHint(20)
                .setEnablePlaceholders(true)
                .build()
            return@Function if (it == FilterState.ARCHIVE_FEEDS) {
                LivePagedListBuilder(archiveFeedLiveData, config).build()
            } else {
                LivePagedListBuilder(newFeedLiveData, config).build()
            }
        })
    val test get() = "from ViewModel@${this.hashCode()} feeds:${newFeeds.value?.size}"
    val archiveMessage: LiveData<HandleOnceEvent<String>> = archiveService.archiveEvent
    val addFilterEvent = filterService.addFilterEvent
    var filterState = FilterState.NEW_FEEDS
        private set
    
    init {
        (filterStateLiveData as MutableLiveData).postValue(filterState)
    }
    
    fun fetchFeeds() {
        val work = OneTimeWorkRequestBuilder<CrawlFeedsWork>().build()
        WorkManager.getInstance()?.let {
            it.beginUniqueWork("fetch_new_feeds", ExistingWorkPolicy.REPLACE, work)
                .enqueue()
        }
    }
    
    fun archiveFeedAtPosition(position: Int) {
        val target = newFeeds.value?.get(position) ?: return
        archiveService.archiveFeed(target.url)
    }
    
    fun undoArchive() {
        archiveService.undoArchive()
    }
    
    fun cancelAddFilter() {
        filterService.undoAdd()
    }
    
    fun showNewFeeds() {
        filterState = FilterState.NEW_FEEDS
        (filterStateLiveData as MutableLiveData).value = filterState
    }
    
    fun showArchiveFeeds() {
        filterState = FilterState.ARCHIVE_FEEDS
        (filterStateLiveData as MutableLiveData).value = filterState
    }
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
    
    enum class FilterState { NEW_FEEDS, ARCHIVE_FEEDS }
}
