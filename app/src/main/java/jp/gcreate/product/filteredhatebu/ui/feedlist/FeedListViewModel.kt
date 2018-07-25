package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.arch.core.util.Function
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import jp.gcreate.product.filteredhatebu.ui.common.StickyHeaderDecoration
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class FeedListViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase,
    private val filterService: FilterService,
    private val archiveService: ArchiveFeedService
) : ViewModel(), StickyHeaderDecoration.GroupCallback {
    
    private val newFeedLiveData = appRoomDatabase.feedDataDao().subscribeFilteredNewFeeds()
    private val archiveFeedLiveData = appRoomDatabase.feedDataDao().subscribeArchivedFeeds()
    val filterStateLiveData: LiveData<FilterState> = MutableLiveData()
    val newFeeds: LiveData<List<FeedData>> = Transformations
        .switchMap(filterStateLiveData, Function {
            return@Function if (it == FilterState.ARCHIVE_FEEDS) {
                archiveFeedLiveData
            } else {
                newFeedLiveData
            }
        })
    val test get() = "from ViewModel@${this.hashCode()} feeds:${newFeeds.value?.size}"
    val archiveMessage: LiveData<HandleOnceEvent<String>> = archiveService.archiveEvent
    val addFilterEvent = filterService.addFilterEvent
    var filterState = FilterState.NEW_FEEDS
        private set
    
    init {
        (filterStateLiveData as MutableLiveData).value = filterState
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
    
    override fun getGroupId(position: Int): Long {
        return newFeeds.value?.let {
            try {
                val date = it[position].fetchedAt.toLocalDate()
                return@let date.run { year * 10000 + monthValue * 100 + dayOfMonth }.toLong()
            } catch (e: ArrayIndexOutOfBoundsException) {
                Timber.w("position $position has ArrayIndexOutOfBoundsException")
                return@let -1L
            } catch (e: IndexOutOfBoundsException) {
                Timber.w("position: $position get IndexOutOfBoundsException")
                return@let -1L
            }
        } ?: -1L
    }
    
    override fun getGroupHeaderText(position: Int): String {
        Timber.v("getGroupHeaderText at position: $position")
        return newFeeds.value?.let {
            return@let it[position].fetchedAt.toLocalDate().toString()
        } ?: ""
    }
    
    override fun isBoundary(position: Int): Boolean {
        if (position == 0) return true
        return newFeeds.value?.let {
            val current = getGroupId(position)
            val prev = getGroupId(position - 1)
            return@let current != prev
        } ?: false
    }
    
    enum class FilterState { NEW_FEEDS, ARCHIVE_FEEDS }
}