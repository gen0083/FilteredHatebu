package jp.gcreate.product.filteredhatebu.presentation.filterdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

@FragmentScope
class FilterDetailViewModel @Inject constructor(
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