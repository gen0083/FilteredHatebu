package jp.gcreate.product.filteredhatebu.presentation.feedlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class FeedListViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) : ViewModel() {

    val newFeeds = appRoomDatabase.feedDataDao().subscribeFilteredNewFeeds()
    val test get() = "from ViewModel@${this.hashCode()} feeds:${newFeeds.value?.size}"
    private val archiveEmitter: MutableLiveData<HandleOnceEvent<String>> = MutableLiveData()
    val archiveMessage: LiveData<HandleOnceEvent<String>> = archiveEmitter
    private var archived: FeedData? = null
    
    fun fetchFeeds() {
        val work = OneTimeWorkRequestBuilder<CrawlFeedsWork>().build()
        WorkManager.getInstance()?.let {
            it.beginUniqueWork("fetch_new_feeds", ExistingWorkPolicy.REPLACE, work)
                .enqueue()
        }
    }
    
    fun archiveFeedAtPosition(position: Int) {
        archived = newFeeds.value?.get(position) ?: return
        archived?.let {
            launch {
                appRoomDatabase.feedDataDao().updateStatusArchived(it.url, true)
                archiveEmitter.postValue(HandleOnceEvent(it.url))
            }
        }
    }
    
    fun undoArchive() {
        archived?.let {
            launch {
                appRoomDatabase.feedDataDao().updateStatusArchived(it.url, false)
                archived = null
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
}