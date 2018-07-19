package jp.gcreate.product.filteredhatebu.ui.favorite

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import javax.inject.Inject

@FragmentScope
class FavoriteViewModel @Inject constructor(appRoomDatabase: AppRoomDatabase) : ViewModel() {
    
    private val feedDataDao = appRoomDatabase.feedDataDao()
    val favoriteFeed: LiveData<List<FeedData>> = feedDataDao.subscribeFavoriteFeeds()
}