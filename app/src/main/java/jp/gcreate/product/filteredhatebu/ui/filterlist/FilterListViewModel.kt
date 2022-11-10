package jp.gcreate.product.filteredhatebu.ui.filterlist

import androidx.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.FilterFeedService

class FilterListViewModel(
    private val db: AppRoomDatabase,
    private val filterFeedService: FilterFeedService,
    private val archiveService: ArchiveFeedService
) : ViewModel() {
    
    private val filteredFeedDao = db.filteredFeedDao()
    val filterInfo = filteredFeedDao.subscribeFilteredInformation()
    val deleteFilterEvent = filterFeedService.deleteFilterEvent
    val archiveEvent = archiveService.archiveEvent
    
    fun undoDeleteFilter() {
        filterFeedService.undoDelete()
    }
}
