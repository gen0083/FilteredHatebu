package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@AppScope
class FeedSaveService @Inject constructor(appRoomDatabase: AppRoomDatabase) {
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val feedFilterDao = appRoomDatabase.feedFilterDao()
    private val filteredFeedDao = appRoomDatabase.filteredFeedDao()
    
    fun saveFeed(feed: HatebuFeedItem): Boolean {
        val feedData = FeedData(url = feed.link, title = feed.title,
                                summary = feed.description ?: "",
                                pubDate = ZonedDateTime.parse(feed.date))
        
        val result = feedDataDao.insertFeed(feedData)
        // Feedの登録が行われなかった場合以下の処理は必要ないので早期リターン
        if (result[0] == -1L) return false
        
        // filterのマッチング処理
        saveAsFilteredFeedIfUrlMatchesAnyFilter(feedData.url)
        return true
    }
    
    fun saveFilter(filter: String) {
        val result = feedFilterDao.insertFilter(FeedFilter(0, filter, ZonedDateTime.now()))
        if (result[0] == -1L) return
        
        // filterを新規追加する場合、すでに保存済みのURLに該当するものがあるかチェックしないといけない
        feedFilterDao.getFilter(filter)?.let {
            saveAsFilteredFeedIfFilterMatchesAnyFeed(it)
        }
    }
    
    private fun saveAsFilteredFeedIfUrlMatchesAnyFilter(url: String) {
        val filters = feedFilterDao.getAllFilters()
        filters.forEach { (id, filter, _) ->
            if (url.contains(filterToRegex(filter))) {
                filteredFeedDao.insertFilteredFeed(FilteredFeed(id, url))
                return
            }
        }
    }
    
    private fun saveAsFilteredFeedIfFilterMatchesAnyFeed(filter: FeedFilter) {
        val feeds = feedDataDao.getAllFeeds()
        val regex = filterToRegex(filter.filter)
        feeds.forEach { feed ->
            if (feed.url.contains(regex)) {
                filteredFeedDao.insertFilteredFeed(FilteredFeed(filter.id, feed.url))
                return
            }
        }
    }
    
    private fun filterToRegex(filter: String): Regex = "https?://[^/]*$filter".toRegex()
}