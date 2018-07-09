package jp.gcreate.product.filteredhatebu.domain

import androidx.work.Worker
import androidx.work.toWorkData
import jp.gcreate.product.filteredhatebu.CustomApplication
import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet
import jp.gcreate.product.filteredhatebu.api.HatenaClient
import jp.gcreate.product.filteredhatebu.domain.services.FeedSaveService
import timber.log.Timber
import javax.inject.Inject

class CrawlFeedsWork : Worker() {
    @Inject lateinit var feedsBurnerClienet: FeedsBurnerClienet
    @Inject lateinit var xmlService: HatenaClient.XmlService
    @Inject lateinit var feedSaveService: FeedSaveService
    
    override fun doWork(): Result {
        // do-crawling
        CustomApplication.getAppComponent(applicationContext)
            .inject(this)
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
                if (feedSaveService.saveFeed(it)) count++
            }
    
            arrayOf("general", "it", "life", "game").forEach { category ->
                val hatebuCategory = xmlService.getCategoryFeed(category).toBlocking().first()
                hatebuCategory.itemList.forEach {
                    Timber.v("save $it")
                    feedSaveService.saveFeed(it)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            outputData = mapOf(KEY_NEW_FEEDS_COUNT to count).toWorkData()
            return Result.FAILURE
        }
        outputData = mapOf(KEY_NEW_FEEDS_COUNT to count).toWorkData()
        return Result.SUCCESS
    }
    
    companion object {
        const val KEY_NEW_FEEDS_COUNT = "new_feeds_count"
    }
}