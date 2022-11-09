package jp.gcreate.product.filteredhatebu.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.jakewharton.threetenabp.AndroidThreeTen
import jp.gcreate.product.filteredhatebu.data.dao.FeedDataDao
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
class FeedDataDaoTest {
    private lateinit var sut: FeedDataDao
    private lateinit var db: AppRoomDatabase
    @get:Rule var executeRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        AndroidThreeTen.init(context)
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = db.feedDataDao()
    }

    @After fun tearDown() {
        db.close()
    }

    @Test
    fun `insertしたらrowIdが返ってくる`() = runTest {
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(0)
        val data = FeedData(
            url = "https://gcreate.jp/",
            title = "test",
            count = 0,
            summary = "test node",
            pubDate = ZonedDateTime.now()
        )
        val first = sut.insertFeed(data)
        assertThat(first.size).isEqualTo(1)
        assertThat(first[0]).isEqualTo(1)

        val second = sut.insertFeed(FeedData(url = "https://test.com/", title = "next",
            summary = "hoge", pubDate = ZonedDateTime.now()))
        assertThat(second.size).isEqualTo(1)
        assertThat(second[0]).isEqualTo(2)
    }

    @Test
    fun `insertで同じUrlを追加したらrowIdにマイナス1が返る`() = runTest {
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(0)
        val data = FeedData(
            url = "https://gcreate.jp/",
            title = "test",
            count = 0,
            summary = "test node",
            pubDate = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        )
        sut.insertFeed(data)
        val first = sut.getAllFeeds()
        assertThat(first.size).isEqualTo(1)
        assertThat(first[0].title).isEqualTo("test")
        val same = FeedData(
            url = "https://gcreate.jp/", title = "hoge", summary = "hoge", count = 0,
            pubDate = ZonedDateTime.now())
        val result = sut.insertFeed(same)
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0]).isEqualTo(-1)
        val second = sut.getAllFeeds()
        assertThat(second.size).isEqualTo(1)
        assertThat(second[0].title).isEqualTo("test")
    }

    @Test
    fun `deleteできる`() = runTest {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", count = 0, summary = "hoge",
            pubDate = ZonedDateTime.now()
        )
        sut.insertFeed(data)
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(1)

        sut.deleteFeed(data)
        val after = sut.getAllFeeds()
        assertThat(after.size).isEqualTo(0)
    }

    @Test
    fun `deleteで存在しないものを指定したら何も起きない`() = runTest {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", count = 0, summary = "hoge",
            pubDate = ZonedDateTime.now()
        )
        sut.insertFeed(data)
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(1)

        sut.deleteFeed(
            FeedData(
                url = "hoge", title = "hoge", count = 0, summary = "hoge",
                pubDate = ZonedDateTime.now()
            )
        )
        val after = sut.getAllFeeds()
        assertThat(after.size).isEqualTo(1)
    }

    @Test
    fun `getAllFeedsでinsertされた新しいデータが取得できる`() = runTest {
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(0)
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", summary = "hoge", count = 0,
            pubDate = ZonedDateTime.now()
        )
        sut.insertFeed(data)
        val beforeNew = sut.getAllFeeds()
        assertThat(beforeNew.size).isEqualTo(1)
    }

    @Test
    fun `updateStatusArchivedで指定したurlがアーカイブできる`() = runTest {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", summary = "hoge", count = 0,
            pubDate = ZonedDateTime.now()
        )
        sut.insertFeed(data)
        val beforeNew = sut.getFilteredNewFeeds()
        assertThat(beforeNew.size).isEqualTo(1)

        sut.updateStatusArchived(data.url, true)
        val afterNew = sut.getFilteredNewFeeds()
        assertThat(afterNew.size).isEqualTo(0)
        val afterAll = sut.getAllFeeds()
        assertThat(afterAll.size).isEqualTo(1)
        assertThat(afterAll[0].isArchived).isTrue
    }

    @Test
    fun `subscribeでinsertごとに新規記事の監視ができる`() = runTest {
        sut.subscribeFilteredNewFeeds().test {
            assertThat(awaitItem().size).isEqualTo(0)

            sut.insertFeed(
                FeedData(
                    url = "https://gcreate.jp/",
                    title = "test",
                    count = 0,
                    summary = "hoge",
                    pubDate = ZonedDateTime.now()
                )
            )
            assertThat(awaitItem().size).isEqualTo(1)
            // insert conflicted id feed
            sut.insertFeed(
                FeedData(
                    url = "https://gcreate.jp/",
                    title = "hoge",
                    count = 0,
                    summary = "hoge",
                    pubDate = ZonedDateTime.now()
                )
            )
            // idがコンフリクトしたときは無視するのでデータに変更はない→LiveDataは更新されない
            cancelAndConsumeRemainingEvents()
        }
    }
}