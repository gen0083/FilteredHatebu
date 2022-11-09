package jp.gcreate.product.filteredhatebu.data

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.jakewharton.threetenabp.AndroidThreeTen
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

/**
 * FeedData　記事データを保存したテーブル
 * FeedFilter　フィルタにかけるURL情報
 * FilteredFeed　フィルタ情報と記事データを結びつけるテーブル
 *
 * 上記3つのDAOの結合テスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FilteredFeedCombinationTest {
    
    private lateinit var db: AppRoomDatabase
    @get:Rule var executeRule = InstantTaskExecutorRule()
    private val FILTERED_FEED_IN_TEST =
        FeedData(url = "https://www.google.com/", title = "test2", summary = "test2 summary",
            pubDate = ZonedDateTime.now(),
            fetchedAt = ZonedDateTime.of(2018, 2, 3, 4, 5, 6, 7, ZoneOffset.UTC))

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        AndroidThreeTen.init(context)
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        db.feedDataDao().insertFeed(
            FeedData(
                url = "https://gcreate.jp/", title = "test1", summary = "test1 summary",
                pubDate = ZonedDateTime.now(),
                fetchedAt = ZonedDateTime.of(2018, 1, 2, 3, 4, 5, 6, ZoneOffset.UTC)
            ),
            FILTERED_FEED_IN_TEST,
//            FeedData(url = "https://www.google.com/", title = "test2", summary = "test2 summary",
//                pubDate = ZonedDateTime.now(),
//                fetchedAt = ZonedDateTime.of(2018, 2, 3, 4, 5, 6, 7, ZoneOffset.UTC)),
            FeedData(url = "https://github.com/gen0083", title = "test3", summary = "test3 summary",
                pubDate = ZonedDateTime.now(),
                fetchedAt = ZonedDateTime.of(2018, 3, 4, 5, 6, 7, 8, ZoneOffset.UTC))
        )
        db.feedFilterDao().insertFilter(FeedFilter(1, "google.com/", ZonedDateTime.now()))
        db.filteredFeedDao().insertFilteredFeed(FilteredFeed(1, "https://www.google.com/"))
    }
    
    @After fun tearDown() {
        db.close()
    }

    @Test
    fun `getFilteredNewFeedsでフィルタされたフィードの一覧が取得できる`() = runTest {
        val notFiltered = db.feedDataDao().getFilteredNewFeeds()
        assertThat(notFiltered.size).isEqualTo(2)
        assertThat(notFiltered[0].title).isEqualToIgnoringCase("test3")
        assertThat(notFiltered[1].title).isEqualToIgnoringCase("test1")
    }

    @Test
    fun `subscribeで新たに追加したフィードを含む新しいListが取得できる`() = runTest {
        db.feedDataDao().subscribeFilteredNewFeeds().test {
            val before = awaitItem()
            assertThat(before.size).isEqualTo(2)
            db.feedDataDao().insertFeed(
                FeedData(
                    url = "https://android.gcreate.jp/", title = "test4", summary = "test4",
                    pubDate = ZonedDateTime.now(),
                    fetchedAt = ZonedDateTime.of(2018, 5, 6, 7, 8, 9, 10, ZoneOffset.UTC)
                )
            )
            val after = awaitItem()
            assertThat(after.size).isEqualTo(3)
            assertThat(after[0].title).isEqualToIgnoringCase("test4")
        }
    }

    @Test
    fun `subscribeで新たにフィルタを追加した結果のリストが取得できる`() = runTest {
        db.feedDataDao().subscribeFilteredNewFeeds().test {
            val test = awaitItem()
            assertThat(test.size).isEqualTo(2)
            assertThat(test[0].title).isEqualToIgnoringCase("test3")
            // フィルタを追加して既存の記事がフィルタされた記事になる
            db.feedFilterDao().insertFilter(FeedFilter(2, "gcreate.jp", ZonedDateTime.now()))
            db.filteredFeedDao().insertFilteredFeed(
                FilteredFeed(2, "https://gcreate.jp/")
            )

            val test2 = awaitItem()
            assertThat(test2.size).isEqualTo(1)
            assertThat(test2[0].title).isEqualToIgnoringCase("test3")
        }
    }

    @Test
    fun `フィルタを削除した場合にフィルタされていた記事が表示されるようになる`() = runTest {
        val before = db.feedDataDao().getFilteredNewFeeds()
        val beforeFiltered = db.filteredFeedDao().getFilteredInformation()
        assertThat(before.size).isEqualTo(2)
        assertThat(beforeFiltered.size).isEqualTo(1)

        db.feedFilterDao().deleteFilter("google.com/")

        val after = db.feedDataDao().getFilteredNewFeeds()
        val afterFiltered = db.filteredFeedDao().getFilteredInformation()
        assertThat(after.size).isEqualTo(3)
        assertThat(afterFiltered.size).isEqualTo(0)
    }

    @Test
    fun `フィルタしたフィードを削除したらフィルタ情報も削除される`() = runTest {
        val before = db.feedDataDao().getAllFeeds()
        val beforeFiltered = db.filteredFeedDao().getFilteredInformation()
        assertThat(before.size).isEqualTo(3)
        assertThat(beforeFiltered.size).isEqualTo(1)

//        db.feedDataDao().deleteFeedByUrl("https://www.google.com/")
        db.feedDataDao().deleteFeed(FILTERED_FEED_IN_TEST)

        val after = db.feedDataDao().getAllFeeds()
        val afterFiltered = db.filteredFeedDao().getFilteredInformation()
        assertThat(after.size).isEqualTo(2)
        assertThat(afterFiltered.size).isEqualTo(0)
    }
}