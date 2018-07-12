package jp.gcreate.product.filteredhatebu.presentation.archive

import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import javax.inject.Inject

@FragmentScope
class ArchivedFeedViewModel @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) : ViewModel() {
    val archivedFeeds = appRoomDatabase.feedDataDao().subscribeArchivedFeeds()
    //TODO: アーカイブしたやつもフィルタした方がいいんじゃないか
    // フィルタの管理（削除とか）できるようにする
    //
}