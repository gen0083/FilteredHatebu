package jp.gcreate.product.filteredhatebu.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZonedDateTime

/**
 * FeedData　記事データを保存したテーブル
 * FeedFilter　フィルタにかけるURL情報
 * FilteredFeed　フィルタ情報と記事データを結びつけるテーブル
 *
 * 上記3つのDAOの結合テスト
 */
class FilteredFeedCombinationTest {
    
    private lateinit var db: AppRoomDatabase
    @get:Rule var executeRule = InstantTaskExecutorRule()
    
    @Before fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        AndroidThreeTen.init(context)
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        
        db.feedDataDao().insertFeed(
            FeedData(url = "https://gcreate.jp/", title = "test1", summary = "test1 summary",
                     pubDate = ZonedDateTime.now()),
            FeedData(url = "https://www.google.com/", title = "test2", summary = "test2 summary",
                     pubDate = ZonedDateTime.now()),
            FeedData(url = "https://github.com/gen0083", title = "test3", summary = "test3 summary",
                     pubDate = ZonedDateTime.now())
        )
        db.feedFilterDao().insertFilter(FeedFilter(1, "google.com/", ZonedDateTime.now()))
        db.filteredFeedDao().insertFilteredFeed(FilteredFeed(1, "https://www.google.com/"))
    }
    
    @After fun tearDown() {
        db.close()
    }
    
    @Test fun get_new_feeds_which_is_not_filtered() {
        val notFiltered = db.feedDataDao().getFilteredNewFeeds()
        assertThat(notFiltered.size).isEqualTo(2)
        assertThat(notFiltered[0].title).isEqualToIgnoringCase("test1")
        assertThat(notFiltered[1].title).isEqualToIgnoringCase("test3")
    }
    
    @Test fun subscribe_live_data_is_updated_when_new_feed_added() {
        val testObserver = mockk<Observer<List<FeedData>>>()
        every { testObserver.onChanged(any()) }.answers { Unit }
        db.feedDataDao().subscribeFilteredNewFeeds().observeForever(testObserver)
        verify(exactly = 1) { testObserver.onChanged(any()) }
        
        db.feedDataDao().insertFeed(
            FeedData(url = "https://android.gcreate.jp/", title = "test4", summary = "test4",
                     pubDate = ZonedDateTime.now())
        )
        val feedDataCapture = slot<List<FeedData>>()
        verify(exactly = 2) { testObserver.onChanged(capture(feedDataCapture)) }
        val captured = feedDataCapture.captured
        assertThat(captured.size).isEqualTo(3)
        assertThat(captured[2].title).isEqualToIgnoringCase("test4")
        
        db.feedDataDao().subscribeFilteredNewFeeds().removeObserver(testObserver)
    }
    
    @Test fun subscribe_live_data_is_updated_when_new_filtered_added() {
        val testObserver = mockk<Observer<List<FeedData>>>()
        every { testObserver.onChanged(any()) }.answers { Unit }
        val feedCapture = slot<List<FeedData>>()
        
        val observer = db.feedDataDao().subscribeFilteredNewFeeds()
        observer.observeForever(testObserver)
        verify(exactly = 1) { testObserver.onChanged(capture(feedCapture)) }
        assertThat(feedCapture.captured.size).isEqualTo(2)
        assertThat(feedCapture.captured[0].title).isEqualToIgnoringCase("test1")
        
        db.feedFilterDao().insertFilter(FeedFilter(2, "gcreate.jp", ZonedDateTime.now()))
        db.filteredFeedDao().insertFilteredFeed(
            FilteredFeed(2, "https://gcreate.jp/")
        )
        
        verify(exactly = 2) { testObserver.onChanged(capture(feedCapture)) }
        assertThat(feedCapture.captured.size).isEqualTo(1)
        assertThat(feedCapture.captured[0].title).isEqualToIgnoringCase("test3")
        
        observer.removeObserver(testObserver)
    }
    
    @Test fun delete_filter_and_filtered_feed_delete_too() {
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
}