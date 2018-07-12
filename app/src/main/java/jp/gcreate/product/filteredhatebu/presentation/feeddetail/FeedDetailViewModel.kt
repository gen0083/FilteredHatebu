package jp.gcreate.product.filteredhatebu.presentation.feeddetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.services.BookmarkCommentsService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import jp.gcreate.product.filteredhatebu.ui.common.LoadingState
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

@FragmentScope
class FeedDetailViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase,
    private val commentsService: BookmarkCommentsService,
    private val filterService: FilterService
) : ViewModel() {
    
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val feedDataEmitter: MutableLiveData<FeedData> = MutableLiveData()
    val feedDetail: LiveData<FeedData> = feedDataEmitter
    private val commentsEmitter: MutableLiveData<HatebuComments> = MutableLiveData()
    val hatebuComments: LiveData<HatebuComments> = commentsEmitter
    private val loadingStateEmitter: MutableLiveData<LoadingState> = MutableLiveData()
    val loadingState: LiveData<LoadingState> = loadingStateEmitter
    var currentUrl: String = ""
        private set
    
    fun fetchFeed(url: String) {
        if (currentUrl == url) return
        currentUrl = url
        loadingStateEmitter.value = LoadingState.LOADING
        launch(CommonPool) {
            val feedData = async { feedDataDao.getFeed(url) }
            val comments = async { commentsService.fetchComments(url) }
            feedDataEmitter.postValue(feedData.await())
            commentsEmitter.postValue(comments.await())
            loadingStateEmitter.postValue(LoadingState.DONE)
        }
    }
    
    fun addFilter(filter: String) {
        filterService.addFilter(filter)
    }
}