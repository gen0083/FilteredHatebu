package jp.gcreate.product.filteredhatebu.domain.services

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

private typealias ArchiveEvent = HandleOnceEvent<String>

@AppScope
class ArchiveFeedService @Inject constructor(
    db: AppRoomDatabase
) {
    
    private val feedDataDao = db.feedDataDao()
    private val archiveEventEmitter = MutableLiveData<ArchiveEvent>()
    val archiveEvent: LiveData<ArchiveEvent> = archiveEventEmitter
    private var archivedUrl: String? = null
    
    fun archiveFeed(url: String) = launch {
        feedDataDao.updateStatusArchived(url, true)
        archivedUrl = url
        archiveEventEmitter.postValue(ArchiveEvent(url))
    }
    
    fun undoArchive() = launch {
        archivedUrl?.let {
            feedDataDao.updateStatusArchived(it, false)
            archivedUrl = null
        }
    }
}