package jp.gcreate.product.filteredhatebu.data.entities

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.dao.FeedFilterDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.ZonedDateTime

class FeedFilterTest {
    private lateinit var sut: FeedFilterDao
    private lateinit var db: AppRoomDatabase
    @get:Rule var executeRule = InstantTaskExecutorRule()
    
    @Before fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        AndroidThreeTen.init(context)
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = db.feedFilterDao()
    }
    
    @After fun tearDown() {
        db.close()
    }
    
    @Test fun `insertに成功するとrowIdが返る`() {
        val result = sut.insertFilter(FeedFilter(1, "hoge", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(1)
        
        val inserted = sut.getNewestFilter()!!
        assertThat(inserted.id).isEqualTo(1)
        assertThat(inserted.filter).isEqualTo("hoge")
    }
    
    @Test fun `insert時にIDを0に指定するとauto_incrementされる`() {
        // 逆に言うとそれ以外の値だと指定したIDでinsertされる
        var result = sut.insertFilter(FeedFilter(0, "hoge", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(1)
        
        val inserted = sut.getFilter("hoge")!!
        assertThat(inserted.id).isEqualTo(1)
        
        result = sut.insertFilter(FeedFilter(0, "fuga", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(2)
        val all = sut.getAllFilters()
        assertThat(all.size).isEqualTo(2)
        assertThat(all[0].id).isEqualTo(1)
        assertThat(all[0].filter).isEqualToIgnoringCase("hoge")
        assertThat(all[1].id).isEqualTo(2)
        assertThat(all[1].filter).isEqualToIgnoringCase("fuga")
    }
    
    @Test fun `insert時にIDを0以外で指定すると指定したIDでinsertされる`() {
        var result = sut.insertFilter(FeedFilter(10, "hoge", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(10)
        val inserted = sut.getFilter("hoge")!!
        assertThat(inserted.id).isEqualTo(10)
        val before = sut.getAllFilters()
        assertThat(before.size).isEqualTo(1)
        
        result = sut.insertFilter(FeedFilter(10, "fuga", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(-1)
        val after = sut.getAllFilters()
        assertThat(after.size).isEqualTo(1)
    }
    
    @Test fun `insert時に0以外のIDを指定し、そのIDが重複した場合insertに失敗する`() {
        // 重複したからインクリメントされるわけではない。auto incrementさせるにはID0のオブジェクトを渡すしかない
        // もしくは、Queryを使ってID以外を指定したinsertを用意するしかない
        var result = sut.insertFilter(FeedFilter(10, "hoge", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(10)
        val before = sut.getAllFilters()
        assertThat(before.size).isEqualTo(1)
        
        result = sut.insertFilter(FeedFilter(10, "fuga", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(-1)
        val after = sut.getAllFilters()
        assertThat(after.size).isEqualTo(1)
    }
    
    @Test fun `insert時にfilterの値が重複している場合マイナス1が返り無視される`() {
        sut.insertFilter(FeedFilter(1, "hoge", ZonedDateTime.now()))
        val before = sut.getAllFilters()
        assertThat(before.size).isEqualTo(1)
        
        val result = sut.insertFilter(FeedFilter(2, "hoge", ZonedDateTime.now()))
        assertThat(result[0]).isEqualTo(-1)
        val after = sut.getAllFilters()
        assertThat(after.size).isEqualTo(1)
    }
    
    @Test fun `getNewestで最後に登録したFilterが取得できる`() {
        sut.insertFilter(FeedFilter(0, "before", ZonedDateTime.now()))
        var newestFilter = sut.getNewestFilter()!!
        assertThat(newestFilter.filter).isEqualToIgnoringCase("before")
        
        sut.insertFilter(FeedFilter(0, "newest", ZonedDateTime.now()))
        newestFilter = sut.getNewestFilter()!!
        assertThat(newestFilter.filter).isEqualToIgnoringCase("newest")
    }
    
    @Test fun `getNewestでフィルタが1件も登録されていない場合nullが返る`() {
        val result = sut.getNewestFilter()
        assertThat(result).isNull()
    }
    
    @Test fun `deleteでFeedFilterを渡す場合IDが一致していれば削除される`() {
        sut.insertFilter(FeedFilter(1, "test", ZonedDateTime.now()))
        val before = sut.getAllFilters()
        assertThat(before.size).isEqualTo(1)
        
        sut.deleteFilter(FeedFilter(1, "hoge", ZonedDateTime.now()))
        val after = sut.getAllFilters()
        assertThat(after.size).isEqualTo(0)
    }
    
    @Test fun `deleteでfilterの値を指定して削除ができる`() {
        sut.insertFilter(FeedFilter(1, "hoge", ZonedDateTime.now()))
        val before = sut.getAllFilters()
        assertThat(before.size).isEqualTo(1)
        
        sut.deleteFilter("hoge")
        val after = sut.getAllFilters()
        assertThat(after.size).isEqualTo(0)
    }
    
    @Test fun `deleteで存在しないfilterを指定して削除しても何も起きない`() {
        sut.insertFilter(FeedFilter(1, "exist", ZonedDateTime.now()))
        val before = sut.getAllFilters()
        assertThat(before.size).isEqualTo(1)
        
        sut.deleteFilter("not exist")
        val after = sut.getAllFilters()
        assertThat(after.size).isEqualTo(1)
    }
}