package jp.gcreate.product.filteredhatebu.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel(appRoomDatabase: AppRoomDatabase) : ViewModel() {

    private val feedDataDao = appRoomDatabase.feedDataDao()
    val favoriteFeed: StateFlow<List<FeedData>> = feedDataDao.subscribeFavoriteFeeds()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
