package jp.gcreate.product.filteredhatebu.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData

class FavoriteViewModel(appRoomDatabase: AppRoomDatabase) : ViewModel() {
    
    private val feedDataDao = appRoomDatabase.feedDataDao()
    val favoriteFeed: LiveData<List<FeedData>> = feedDataDao.subscribeFavoriteFeeds()
}
