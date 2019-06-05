package jp.gcreate.product.filteredhatebu.data.entities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.dao.FilteredFeedDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZonedDateTime

class FilteredFeedTest {
    private lateinit var sut: FilteredFeedDao
    private lateinit var db: AppRoomDatabase
    @get:Rule var executeRule = InstantTaskExecutorRule()
    
    @Before fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        AndroidThreeTen.init(context)
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = db.filteredFeedDao()
        
        db.feedDataDao().insertFeed(
            FeedData(url = "https://gcreate.jp/",
                                                                     title = "test1",
                                                                     summary = "hoge",
                                                                     pubDate = ZonedDateTime.now()),
            FeedData(
                url = "https://wantit.gcreate.jp/", title = "test2", summary = "fuga",
                pubDate = ZonedDateTime.now()),
            FeedData(
                url = "https://github.com/gen0083/", title = "test3", summary = "foo",
                pubDate = ZonedDateTime.now())
        )
        db.feedFilterDao().insertFilter(
            FeedFilter(id = 1, filter = "wantit.gcreate.jp", createdAt = ZonedDateTime.now())
        )
        sut.insertFilteredFeed(
            FilteredFeed(1, "https://wantit.gcreate.jp/")
        )
    }
    
    @After fun tearDown() {
        db.close()
    }
    
    @Test fun getFilteredInfo() {
        val info = sut.getFilteredInformation()
        assertThat(info.size).isEqualTo(1)
        assertThat(info[0].feedCount).isEqualTo(1)
        assertThat(info[0].filter).isEqualTo("wantit.gcreate.jp")
    }
    
    @Test fun getFilteredInfo_as_2() {
        sut.insertFilteredFeed(FilteredFeed(1, "https://gcreate.jp/"))
        val info = sut.getFilteredInformation()
        assertThat(info.size).isEqualTo(1)
        assertThat(info[0].feedCount).isEqualTo(2)
        assertThat(info[0].filter).isEqualTo("wantit.gcreate.jp")
    }
}