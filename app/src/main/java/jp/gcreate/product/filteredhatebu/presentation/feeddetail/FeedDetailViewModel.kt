package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import javax.inject.Inject

@ActivityScope
class FeedDetailViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) : ViewModel() {
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val mutableFeedLiveData: MutableLiveData<FeedData> = MutableLiveData()
    val feedDetail: LiveData<FeedData> = mutableFeedLiveData
    
    fun fetchFeed(url: String) {
    }
}