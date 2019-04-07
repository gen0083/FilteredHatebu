package jp.gcreate.product.filteredhatebu.domain.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private typealias ArchiveEvent = HandleOnceEvent<String>

class ArchiveFeedService(db: AppRoomDatabase) {
    
    private val feedDataDao = db.feedDataDao()
    private val archiveEventEmitter = MutableLiveData<ArchiveEvent>()
    val archiveEvent: LiveData<ArchiveEvent> = archiveEventEmitter
    private var archivedUrl: String? = null
    
    fun archiveFeed(url: String) = GlobalScope.launch {
        feedDataDao.updateStatusArchived(url, true)
        archivedUrl = url
        archiveEventEmitter.postValue(ArchiveEvent(url))
    }
    
    fun undoArchive() = GlobalScope.launch {
        archivedUrl?.let {
            feedDataDao.updateStatusArchived(it, false)
            archivedUrl = null
        }
    }
}
