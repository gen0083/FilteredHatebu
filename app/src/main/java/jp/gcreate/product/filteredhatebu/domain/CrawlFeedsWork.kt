package jp.gcreate.product.filteredhatebu.domain

import androidx.work.Worker
import androidx.work.toWorkData
import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog
import jp.gcreate.product.filteredhatebu.ext.getAppComponent
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

class CrawlFeedsWork : Worker() {
    @Inject lateinit var feedsBurnerClienet: FeedsBurnerClienet
    @Inject lateinit var xmlService: HatenaClient.XmlService
    @Inject lateinit var appRoomDatabase: AppRoomDatabase
    @Inject lateinit var notificationUtil: NotificationUtil
    
    override fun doWork(): Result {
        // do-crawling
        applicationContext.getAppComponent().inject(this)
        Timber.d("do-work on thread: ${Thread.currentThread()}")
        
        var count = 0
        try {
            // はてなブックマーク総合ホットエントリを取得
            val hatebuSougou = feedsBurnerClienet.hotentryFeed.toBlocking().first()
            hatebuSougou.itemList.forEach {
                Timber.v("save $it")
                if (saveFeed(it)) count++
            }
            // 各カテゴリごとのはてなブックマークホットエントリを取得
            arrayOf("general", "it", "life", "game").forEach { category ->
                val hatebuCategory = xmlService.getCategoryFeed(category).toBlocking().first()
                hatebuCategory.itemList.forEach {
                    Timber.v("save $it")
                    if (saveFeed(it)) count++
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            outputData = mapOf(KEY_NEW_FEEDS_COUNT to count).toWorkData()
            logWorkResult(count)
            return Result.FAILURE
        }
        outputData = mapOf(KEY_NEW_FEEDS_COUNT to count).toWorkData()
        logWorkResult(count)
        return Result.SUCCESS
    }
    
    private fun logWorkResult(count: Int) {
        val type = inputData.getString(KEY_TYPE) ?: "one_time"
        val tag = tags.joinToString()
        appRoomDatabase.workLogDao()
            .insert(WorkLog(0, ZonedDateTime.now(), "tag<$tag> type:$type, new feeds=$count"))
        if (type == "period") {
            notificationUtil.notifyNewFeedsCount(count)
        }
    }
    
    fun saveFeed(feed: HatebuFeedItem): Boolean {
        val feedData = FeedData(url = feed.link, title = feed.title,
                                count = feed.count ?: 0,
                                summary = feed.description ?: "",
                                pubDate = ZonedDateTime.parse(feed.date),
                                fetchedAt = ZonedDateTime.now())
        val feedDataDao = appRoomDatabase.feedDataDao()
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
        val filters = appRoomDatabase.feedFilterDao().getAllFilters()
        filters.forEach { (id, filter, _) ->
            if (url.contains(filterToRegex(filter))) {
                appRoomDatabase.filteredFeedDao().insertFilteredFeed(FilteredFeed(id, url))
                return
            }
        }
    }
    
    private fun filterToRegex(filter: String): Regex = "https?://[^/]*$filter".toRegex()
    
    companion object {
        const val KEY_NEW_FEEDS_COUNT = "new_feeds_count"
        const val KEY_TYPE = "key_type"
    }
}