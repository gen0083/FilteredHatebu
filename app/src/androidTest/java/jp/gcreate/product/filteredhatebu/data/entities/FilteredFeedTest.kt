package jp.gcreate.product.filteredhatebu.data.entities

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jakewharton.threetenabp.AndroidThreeTen
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.dao.FilteredFeedDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class FilteredFeedTest {
    private lateinit var sut: FilteredFeedDao
    private lateinit var db: AppRoomDatabase

    @get:Rule
    var executeRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlocking {
        // filterはdomainのservicesで管理するから細かくはそっちでテストするべき
        // dbにfilterを追加して、そのフィルタを元にfilteredFeedが追加されてとか
        // dbがどう実装されてるか把握してないといけないテストはどうなんだという気がしている
        val context = ApplicationProvider.getApplicationContext<Context>()
        AndroidThreeTen.init(context)
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = db.filteredFeedDao()

        db.feedDataDao().insertFeed(
            FeedData(
                url = "https://gcreate.jp/",
                title = "test1",
                summary = "hoge",
                pubDate = ZonedDateTime.now()
            ),
            FeedData(
                url = "https://wantit.gcreate.jp/", title = "test2", summary = "fuga",
                pubDate = ZonedDateTime.now()
            ),
            FeedData(
                url = "https://github.com/gen0083/", title = "test3", summary = "foo",
                pubDate = ZonedDateTime.now()
            )
        )
        db.feedFilterDao().insertFilter(
            FeedFilter(id = 1, filter = "wantit.gcreate.jp", createdAt = ZonedDateTime.now())
        )
        sut.insertFilteredFeed(
            FilteredFeed(1, "https://wantit.gcreate.jp/")
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `getFilteredInformationでフィルタ文字列と該当フィルタによってフィルタリングされている件数が取得できる`() = runTest {
        val info = sut.getFilteredInformation()
        assertThat(info.size).isEqualTo(1)
        assertThat(info[0].feedCount).isEqualTo(1)
        assertThat(info[0].filter).isEqualTo("wantit.gcreate.jp")
    }
}