package jp.gcreate.product.filteredhatebu.ui.filterdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import kotlinx.coroutines.experimental.launch

class FilterDetailViewModel(
    private val db: AppRoomDatabase,
    private val filterService: FilterService
) : ViewModel() {
    
    private val filteredFeedDao = db.filteredFeedDao()
    private val filteredFeedListEmitter = MutableLiveData<List<FeedData>>()
    val filteredFeedList: LiveData<List<FeedData>> = filteredFeedListEmitter
    private var currentFilter: String? = null
    
    fun fetchFeeds(filter: String) = launch {
        currentFilter = filter
        val list = filteredFeedDao.getFilteredFeeds(filter)
        filteredFeedListEmitter.postValue(list)
    }
    
    fun deleteFilter() {
        currentFilter?.let { filterService.deleteFilter(it) }
    }
}
