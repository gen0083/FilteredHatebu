package jp.gcreate.product.filteredhatebu.presentation.filterdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

@FragmentScope
class FilterDetailViewModel @Inject constructor(
    private val db: AppRoomDatabase
) : ViewModel() {
    
    private val filteredFeedDao = db.filteredFeedDao()
    private val filteredFeedListEmitter = MutableLiveData<List<FeedData>>()
    val filteredFeedList: LiveData<List<FeedData>> = filteredFeedListEmitter
    
    fun fetchFeeds(filter: String) = launch {
        val list = filteredFeedDao.getFilteredFeeds(filter)
        filteredFeedListEmitter.postValue(list)
    }
}