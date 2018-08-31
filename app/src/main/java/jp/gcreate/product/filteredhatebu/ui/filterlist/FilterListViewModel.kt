package jp.gcreate.product.filteredhatebu.ui.filterlist

import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService

class FilterListViewModel(
    private val db: AppRoomDatabase,
    private val filterService: FilterService,
    private val archiveService: ArchiveFeedService
) : ViewModel() {
    
    private val filteredFeedDao = db.filteredFeedDao()
    val filterInfo = filteredFeedDao.subscribeFilteredInformation()
    val deleteFilterEvent = filterService.deleteFilterEvent
    val archiveEvent = archiveService.archiveEvent
    
    fun undoDeleteFilter() {
        filterService.undoDelete()
    }
}
