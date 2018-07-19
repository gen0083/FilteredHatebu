package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@AppScope
class FeedFetchService @Inject constructor(appRoomDatabase: AppRoomDatabase) {
    private val feedDataDao = appRoomDatabase.feedDataDao()
    private val feedFilterDao = appRoomDatabase.feedFilterDao()
    private val filteredFeedDao = appRoomDatabase.filteredFeedDao()
    
    fun saveFeed(feed: HatebuFeedItem): Boolean {
        val feedData = FeedData(url = feed.link, title = feed.title,
                                count = feed.count ?: 0,
                                summary = feed.description ?: "",
                                pubDate = ZonedDateTime.parse(feed.date))
        
        val result = feedDataDao.insertFeed(feedData)
        // Feedの登録が行われなかった場合ははてブ数の更新を行うだけ
        if (result[0] == -1L) {
            feedDataDao.updateHatebuCount(feed.link, feed.count)
            return false
        }
        
        // filterのマッチング処理
        saveAsFilteredFeedIfUrlMatchesAnyFilter(feedData.url)
        return true
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
    
    private fun filterToRegex(filter: String): Regex = "https?://[^/]*$filter".toRegex()
}