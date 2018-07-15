package jp.gcreate.product.filteredhatebu.presentation.filterlist

import android.arch.lifecycle.ViewModel
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.di.Scope.FragmentScope
import javax.inject.Inject

@FragmentScope
class FilterListViewModel @Inject constructor(
    private val db: AppRoomDatabase
) : ViewModel() {
    
    private val filteredFeedDao = db.filteredFeedDao()
}