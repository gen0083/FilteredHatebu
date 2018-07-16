package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import javax.inject.Inject

@FragmentScope
class FilterListViewModel @Inject constructor(
    private val db: AppRoomDatabase,
    private val filterService: FilterService
) : ViewModel() {
    
    private val filteredFeedDao = db.filteredFeedDao()
    val filterInfo = filteredFeedDao.subscribeFilteredInformation()
    val deleteFilterEvent = filterService.deleteFilterEvent
    
    fun undoDeleteFilter() {
        filterService.undoDelete()
    }
}