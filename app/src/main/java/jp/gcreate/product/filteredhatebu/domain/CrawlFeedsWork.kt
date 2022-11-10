package jp.gcreate.product.filteredhatebu.domain

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClient
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import jp.gcreate.product.filteredhatebu.api.response.HatebuFeedItem
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.domain.services.FilterFeedService
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

class CrawlFeedsWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params),
    KoinComponent {

    private val feedsBurnerClient: FeedsBurnerClient by inject()
    private val xmlService: HatenaClient.XmlService by inject()
    private val notificationUtil: NotificationUtil by inject()
    private val filterFeedService: FilterFeedService by inject()

    override suspend fun doWork(): Result {
        // do-crawling
        Timber.d("do-work on thread: ${Thread.currentThread()}")

        var count = 0
        try {
            // はてなブックマーク総合ホットエントリを取得
            val hatebuSougou = feedsBurnerClient.getHotentryFeed()
            hatebuSougou.itemList.forEach {
                Timber.v("save $it")
                if (saveFeed(it)) count++
            }
            // 各カテゴリごとのはてなブックマークホットエントリを取得
            arrayOf("general", "it", "life", "game").forEach { category ->
                val hatebuCategory = xmlService.getCategoryFeed(category)
                hatebuCategory.itemList.forEach {
                    Timber.v("save $it")
                    if (saveFeed(it)) count++
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            val outputData = workDataOf(KEY_NEW_FEEDS_COUNT to count)
            logWorkResult(count)
            return Result.failure(outputData)
        }
        val outputData = workDataOf(KEY_NEW_FEEDS_COUNT to count)
        logWorkResult(count)
        return Result.success(outputData)
    }

    private fun logWorkResult(count: Int) {
        val type = inputData.getString(KEY_TYPE) ?: "one_time"
        val tag = tags.joinToString()
        //        appRoomDatabase.workLogDao()
        //            .insert(WorkLog(0, ZonedDateTime.now(), "tag<$tag> type:$type, new feeds=$count"))
        if (type == "period") {
            notificationUtil.notifyNewFeedsCount(count)
        }
    }

    suspend fun saveFeed(feed: HatebuFeedItem): Boolean = withContext(Dispatchers.IO) {
        val feedData = FeedData(
            url = feed.link, title = feed.title,
            count = feed.count,
            summary = feed.description ?: "",
            pubDate = ZonedDateTime.parse(feed.date),
            fetchedAt = ZonedDateTime.now()
        )
        return@withContext filterFeedService.saveFeed(feedData)
    }

    companion object {
        const val KEY_NEW_FEEDS_COUNT = "new_feeds_count"
        const val KEY_TYPE = "key_type"
    }
}
