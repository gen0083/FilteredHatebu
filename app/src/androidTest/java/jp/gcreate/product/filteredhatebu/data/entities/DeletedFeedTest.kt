package jp.gcreate.product.filteredhatebu.data.entities

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.dao.DeletedFeedDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class DeletedFeedTest {
    lateinit var db: AppRoomDatabase
    lateinit var sut: DeletedFeedDao
    
    @Before fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = db.deletedFeedDao()
    }
    
    @After fun tearDown() {
        db.close()
    }
    
    @Test fun `指定したURLが削除テーブルに存在する場合はtrueを返す`() {
        sut.addDeletedUrl(DeletedFeed("https://gcreate.jp/"))
        
        assertThat(sut.isDeletedUrl("https://gcreate.jp/")).isTrue()
    }
    
    @Test fun `指定したURLが削除テーブルにない場合はfalseを返す`() {
        assertThat(sut.isDeletedUrl("https://google.com/")).isFalse()
    }
    
    @Test fun `複数テスト`() {
        sut.addDeletedUrl(DeletedFeed("https://hoge.jp"))
        sut.addDeletedUrl(DeletedFeed("http://gcreate.jp"))
        
        assertThat(sut.isDeletedUrl("https://gcreate.jp/")).isFalse()
        assertThat(sut.isDeletedUrl("http://gcreate.jp")).isTrue()
        assertThat(sut.isDeletedUrl("http://gcreate.jp/")).isFalse()
        assertThat(sut.isDeletedUrl("https://hoge.jp")).isTrue()
        assertThat(sut.isDeletedUrl("hoge")).isFalse()
    }
}