package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import jp.gcreate.product.filteredhatebu.domain.services.ArchiveFeedService
import jp.gcreate.product.filteredhatebu.domain.services.BookmarkCommentsService
import jp.gcreate.product.filteredhatebu.domain.services.FilterService
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import jp.gcreate.product.filteredhatebu.ui.common.HandleOnceEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject

@FragmentScope
class FeedDetailViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase,
    private val commentsService: BookmarkCommentsService,
    private val filterService: FilterService,
    private val archiveService: ArchiveFeedService
) : ViewModel() {
    
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val feedDataEmitter: MutableLiveData<FeedData> = MutableLiveData()
    val feedDetail: LiveData<FeedData> = feedDataEmitter
    private val commentsEmitter: MutableLiveData<HatebuComments> = MutableLiveData()
    val hatebuComments: LiveData<HatebuComments> = commentsEmitter
    private val addFilterActionEmitter: MutableLiveData<HandleOnceEvent<String>> = MutableLiveData()
    val addFilterAction: LiveData<HandleOnceEvent<String>> = addFilterActionEmitter
    var currentUrl: String = ""
        private set
    
    fun fetchFeed(url: String) {
        Timber.d("test: $this")
        if (currentUrl == url) return
        currentUrl = url
        commentsEmitter.value = HatebuComments.Loading
        launch(CommonPool) {
            val feedData = async { feedDataDao.getFeed(url) }
            val comments = async { commentsService.fetchComments(url) }
            feedDataEmitter.postValue(feedData.await())
            commentsEmitter.postValue(comments.await())
        }
    }
    
    fun addFilter(filter: String) {
        filterService.addFilter(filter)
        addFilterActionEmitter.value = HandleOnceEvent(filter)
    }
    
    fun archiveFeed() {
        archiveService.archiveFeed(currentUrl)
    }
    
    fun favoriteFeed() {
        val isFavorite = feedDetail.value?.isFavorite ?: false
        val current = feedDetail.value ?: throw IllegalStateException("current feed is null")
        launch(CommonPool) {
            val updated = current.copy(isFavorite = !current.isFavorite)
            feedDataDao.updateStatusFavorite(updated.url, updated.isFavorite)
            feedDataEmitter.postValue(updated)
        }
    }
}