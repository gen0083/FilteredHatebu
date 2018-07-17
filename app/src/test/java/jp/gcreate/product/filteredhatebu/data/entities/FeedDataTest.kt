package jp.gcreate.product.filteredhatebu.data.entities

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.threeten.bp.ZonedDateTime

class FeedDataIsMatchFilterTest {
    @Test fun `ドメインが一致していればtrueを返す`() {
        val sut = makeDummyData("http://test.com/hoge/fuga")
        assertThat(sut.isMatchFilter("test.com/")).isTrue()
    }
    
    @Test fun `サブドメインが含まれていてもドメインの末尾が一致すればtrueを返す`() {
        val sut = makeDummyData("http://www.test.com/hoge/fuga")
        assertThat(sut.isMatchFilter("test.com/")).isTrue()
    }
    
    @Test fun `ドメインではない部分にフィルタ文字列が含まれているだけの場合はfalseを返す`() {
        val sut = makeDummyData("http://www.test.jp/hoge/test.com/")
        assertThat(sut.isMatchFilter("test.com/")).isFalse()
    }
    
    @Test fun `サブディレクトリを含むフィルタがサブディレクトリを含めて一致すればtrueを返す`() {
        val sut = makeDummyData("https://gcreate.jp/test/abc.html")
        assertThat(sut.isMatchFilter("gcreate.jp/test/")).isTrue()
    }
    
    @Test fun `サブディレクトリを含むフィルタはドメイン部分のみの一致ではfalseを返す`() {
        val sut = makeDummyData("https://gcreate.jp/hoge/abc.html")
        assertThat(sut.isMatchFilter("gcreate.jp/test/")).isFalse()
    }
    
    @Test fun `サブディレクトリを含むフィルタがドメイン以外の部分に含まれるだけならfalseを返す`() {
        val sut = makeDummyData("https://gcreate.jp/hoge/abc.html?url=gcreate.jp/test/abc.htlm")
        assertThat(sut.isMatchFilter("gcreate.jp/test/")).isFalse()
    }
    
    @Test fun `httpsでもドメイン部分さえ一致していればtrueを返す`() {
        val http = makeDummyData("http://gcreate.jp/hoge/")
        val https = makeDummyData("https://gcreate.jp/hoge/")
        val filter = "gcreate.jp/"
        
        assertThat(http.isMatchFilter(filter)).isTrue()
        assertThat(https.isMatchFilter(filter)).isTrue()
    }
    
    private fun makeDummyData(url: String) = FeedData(url = url, title = "", summary = "",
                                                      pubDate = ZonedDateTime.now())
}