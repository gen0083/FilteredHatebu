package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private typealias ArchiveEvent = HandleOnceEvent<String>

class ArchiveFeedService(db: AppRoomDatabase) {

    private val feedDataDao = db.feedDataDao()
    private val archiveEventEmitter: MutableStateFlow<ArchiveEvent?> = MutableStateFlow(null)
    val archiveEvent: StateFlow<ArchiveEvent?> = archiveEventEmitter
    private var archivedUrl: String? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun archiveFeed(url: String) = scope.launch {
        feedDataDao.updateStatusArchived(url, true)
        archivedUrl = url
        archiveEventEmitter.emit(ArchiveEvent(url))
    }

    fun undoArchive() = scope.launch {
        archivedUrl?.let {
            feedDataDao.updateStatusArchived(it, false)
            archivedUrl = null
        }
    }
}
