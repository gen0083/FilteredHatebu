package jp.gcreate.product.filteredhatebu.data.entities

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.dao.DeletedFeedDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeletedFeedTest {
    lateinit var db: AppRoomDatabase
    lateinit var sut: DeletedFeedDao

    @Before fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = db.deletedFeedDao()
    }

    @After fun tearDown() {
        db.close()
    }

    @Test
    fun `指定したURLが削除テーブルに存在する場合はtrueを返す`() = runTest {
        sut.addDeletedUrl(DeletedFeed("https://gcreate.jp/"))

        assertThat(sut.isDeletedUrl("https://gcreate.jp/")).isTrue
    }

    @Test
    fun `指定したURLが削除テーブルにない場合はfalseを返す`() = runTest {
        assertThat(sut.isDeletedUrl("https://google.com/")).isFalse
    }

    @Test
    fun `複数パターンテスト`() = runTest {
        sut.addDeletedUrl(DeletedFeed("https://hoge.jp"))
        sut.addDeletedUrl(DeletedFeed("http://gcreate.jp"))

        // httpsとhttpが違うとfalse
        assertThat(sut.isDeletedUrl("https://gcreate.jp/")).isFalse
        // 完全一致
        assertThat(sut.isDeletedUrl("http://gcreate.jp")).isTrue
        // 最後のスラッシュの有無でfalse
        assertThat(sut.isDeletedUrl("http://gcreate.jp/")).isFalse
        // 完全一致
        assertThat(sut.isDeletedUrl("https://hoge.jp")).isTrue
        // 部分文字列でfalse
        assertThat(sut.isDeletedUrl("hoge")).isFalse
    }
}