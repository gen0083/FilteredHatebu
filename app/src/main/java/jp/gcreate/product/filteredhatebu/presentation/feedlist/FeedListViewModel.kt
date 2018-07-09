package jp.gcreate.product.filteredhatebu.presentation.feedlist

import android.arch.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import jp.gcreate.product.filteredhatebu.domain.CrawlFeedsWork
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class FeedListViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) : ViewModel() {

    val newFeeds = appRoomDatabase.feedDataDao().subscribeFilteredNewFeeds()
    val test get() = "from ViewModel@${this.hashCode()} feeds:${newFeeds.value?.size}"
    
    fun fetchFeeds() {
        val work = OneTimeWorkRequestBuilder<CrawlFeedsWork>().build()
        WorkManager.getInstance()?.let {
            it.beginUniqueWork("fetch_new_feeds", ExistingWorkPolicy.REPLACE, work)
                .enqueue()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
}