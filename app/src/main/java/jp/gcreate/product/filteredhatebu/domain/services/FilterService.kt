package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import javax.inject.Inject

@AppScope
class FilterService @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
) {
    private val feedFilterDao = appRoomDatabase.feedFilterDao()
}
