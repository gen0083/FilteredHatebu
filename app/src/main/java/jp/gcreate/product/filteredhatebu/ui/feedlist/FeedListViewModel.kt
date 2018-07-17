package jp.gcreate.product.filteredhatebu.ui.feedlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class FeedListViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase,
    private val filterService: FilterService,
    private val archiveService: ArchiveFeedService
) : ViewModel() {

    val newFeeds = appRoomDatabase.feedDataDao().subscribeFilteredNewFeeds()
    val test get() = "from ViewModel@${this.hashCode()} feeds:${newFeeds.value?.size}"
    private val archiveEmitter: MutableLiveData<HandleOnceEvent<String>> = MutableLiveData()
    val archiveMessage: LiveData<HandleOnceEvent<String>> = archiveService.archiveEvent
    val addFilterEvent = filterService.addFilterEvent
    
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
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
}