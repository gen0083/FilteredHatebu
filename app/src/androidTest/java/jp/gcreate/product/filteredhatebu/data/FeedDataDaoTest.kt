package jp.gcreate.product.filteredhatebu.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jakewharton.threetenabp.AndroidThreeTen
import io.mockk.*
import jp.gcreate.product.filteredhatebu.data.dao.FeedDataDao
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

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
    
    @Test fun insert_then_return_row_id_inserted_rowId() {
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(0)
        val data = FeedData(
            url = "https://gcreate.jp/",
            title = "test",
            count = 0,
            summary = "test node",
            pubDate = ZonedDateTime.now())
        val first = sut.insertFeed(data)
        assertThat(first.size).isEqualTo(1)
        assertThat(first[0]).isEqualTo(1)
        
        val second = sut.insertFeed(FeedData(url = "https://test.com/", title = "next",
                                             summary = "hoge", pubDate = ZonedDateTime.now()))
        assertThat(second.size).isEqualTo(1)
        assertThat(second[0]).isEqualTo(2)
    }
    
    @Test fun insert_same_url_feed_then_return_row_id_minus1() {
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(0)
        val data = FeedData(
            url = "https://gcreate.jp/",
            title = "test",
            count = 0,
            summary = "test node",
            pubDate = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
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
    
    @Test fun delete_test() {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", count = 0, summary = "hoge",
            pubDate = ZonedDateTime.now())
        sut.insertFeed(data)
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(1)
        
        sut.deleteFeed(data)
        val after = sut.getAllFeeds()
        assertThat(after.size).isEqualTo(0)
    }
    
    @Test fun delete_when_id_not_exist() {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", count = 0, summary = "hoge",
            pubDate = ZonedDateTime.now())
        sut.insertFeed(data)
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(1)
        
        sut.deleteFeed(
            FeedData(url = "hoge", title = "hoge", count = 0, summary = "hoge",
                     pubDate = ZonedDateTime.now()))
        val after = sut.getAllFeeds()
        assertThat(after.size).isEqualTo(1)
    }
    
    @Test fun get_new_feeds_list() {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", summary = "hoge", count = 0,
            pubDate = ZonedDateTime.now())
        sut.insertFeed(data)
        val before = sut.getAllFeeds()
        assertThat(before.size).isEqualTo(1)
        val beforeNew = sut.getFilteredNewFeeds()
        assertThat(beforeNew.size).isEqualTo(1)
    }
    
    @Test fun update_archive_flag() {
        val data = FeedData(
            url = "https://gcreate.jp/", title = "test", summary = "hoge", count = 0,
            pubDate = ZonedDateTime.now())
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
    
    @Test fun subscribe_called_every_insert() {
        val mockObserver = mockk<Observer<List<FeedData>>>()
        every { mockObserver.onChanged(any()) } just Runs
        sut.subscribeFilteredNewFeeds().observeForever(mockObserver)
        verify(exactly = 1) { mockObserver.onChanged(any()) }
        
        sut.insertFeed(
            FeedData(url = "https://gcreate.jp/",
                     title = "test",
                     count = 0,
                     summary = "hoge",
                     pubDate = ZonedDateTime.now()))
        verify(exactly = 2) { mockObserver.onChanged(any()) }
        // insert conflicted id feed
        sut.insertFeed(
            FeedData(url = "https://gcreate.jp/",
                     title = "hoge",
                     count = 0,
                     summary = "hoge",
                     pubDate = ZonedDateTime.now()))
        // idがコンフリクトしたときは無視するのでデータに変更はない→LiveDataは更新されない
        verify(exactly = 2) { mockObserver.onChanged(any()) }
    
        sut.subscribeFilteredNewFeeds().removeObserver(mockObserver)
    }
}