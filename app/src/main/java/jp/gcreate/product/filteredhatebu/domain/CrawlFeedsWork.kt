package jp.gcreate.product.filteredhatebu.domain

import androidx.work.Worker
import androidx.work.toWorkData
import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog
import jp.gcreate.product.filteredhatebu.domain.services.FeedFetchService
import jp.gcreate.product.filteredhatebu.ext.getAppComponent
import jp.gcreate.product.filteredhatebu.ui.common.NotificationUtil
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

class CrawlFeedsWork : Worker() {
    @Inject lateinit var feedsBurnerClienet: FeedsBurnerClienet
    @Inject lateinit var xmlService: HatenaClient.XmlService
    @Inject lateinit var feedFetchService: FeedFetchService
    @Inject lateinit var appRoomDatabase: AppRoomDatabase
    @Inject lateinit var notificationUtil: NotificationUtil
    
    override fun doWork(): Result {
        // do-crawling
        applicationContext.getAppComponent().inject(this)
        Timber.d("do-work on thread: ${Thread.currentThread()}")
        
        // 処理フロー
        // 1. 各URLから記事のリストを取得する（このアプリではFeedBurnerClientからホットエントリ総合を
        // 　　HatenaClient.XmlServiceから各カテゴリごとのホットエントリを）
        // 2. 取得した記事をFeedDataに登録
        // 3. フィルタとのマッチング処理（登録する1つ1つの記事ごとに処理を行う）
        // 3.a FeedFilterの一覧を取得
        // 3.b FeedFilter一覧から正規表現を使って登録した記事のURLがマッチするかを判定
        // 3.c マッチした場合FilteredFeedに登録
        // 4. すべての記事について実行が終了したらクロール処理を終了する
        var count = 0
        try {
            val hatebuSougou = feedsBurnerClienet.hotentryFeed.toBlocking().first()
            hatebuSougou.itemList.forEach {
                Timber.v("save $it")
                if (feedFetchService.saveFeed(it)) count++
            }
    
            arrayOf("general", "it", "life", "game").forEach { category ->
                val hatebuCategory = xmlService.getCategoryFeed(category).toBlocking().first()
                hatebuCategory.itemList.forEach {
                    Timber.v("save $it")
                    feedFetchService.saveFeed(it)
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
    
    companion object {
        const val KEY_NEW_FEEDS_COUNT = "new_feeds_count"
        const val KEY_TYPE = "key_type"
    }
}