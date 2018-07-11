package jp.gcreate.product.filteredhatebu.presentation.archive

import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.ActivityScope
import javax.inject.Inject

@ActivityScope
class ArchivedFeedViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) : ViewModel() {
    val archivedFeeds = appRoomDatabase.feedDataDao().subscribeArchivedFeeds()
}