package jp.gcreate.product.filteredhatebu

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.regex.Pattern

/**
 * Copyright 2016 G-CREATE
 */
class RegexTest {

    @Test
    fun test() {
        val p = Pattern.compile("test\\.com")
        val m = p.matcher("http://www.test.com/")
        assertThat(m.find()).isTrue()
    }

    @Test
    fun パターンを文字列連結() {
        val filter = "test.com"
        val p = Pattern.compile("http(s)?://[^/]*$filter/")
        var m = p.matcher("http://www.test.com/anything")
        assertThat(m.find()).isTrue()
        m = p.matcher("http://hoge.com/test.com/")
        assertThat(m.find()).isFalse()
        m = p.matcher("http://test.com/abc/")
        assertThat(m.find()).isTrue()
    }

    @Test
    fun ドメイン名を抽出する() {
        val p = Pattern.compile("http://(.+?/)")
        var m = p.matcher("http://www.hoge.com/fuga/something")
        if (m.find()) {
            assertThat(m.groupCount()).isEqualTo(1)
            assertThat(m.group(1)).isEqualTo("www.hoge.com/")
        }

        m = p.matcher("http://fuga.com/hoge/test/some/any/something")
        m.find()
        assertThat(m.group(1)).isEqualTo("fuga.com/")
    }

    @Test
    fun ドメイン名_サブディレクトリを抽出する() {
        val p = Pattern.compile("http://(.+?/.+?/)")
        val m = p.matcher("http://hoge.com/test/abc/def/index.html")
        m.find()
        assertThat(m.group(1)).isEqualTo("hoge.com/test/")
    }

    @Test
    fun サブドメインを消す() {
        val target = "www.hoge.fuga.co.jp/"
        val splited = target.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        assertThat(splited.size).isEqualTo(5)
    }
    
    @Test fun `patternとregexを使ったマッチング`() {
        val pattern = Pattern.compile("https?://[^/]*hoge.jp/")
        val regex = "https?://[^/]*hoge.jp/".toRegex()
        
        val target = "https://www.hoge.jp/fuga/index.html"
        
        val matcher = pattern.matcher(target)
        val patternResult = matcher.find()
        assertThat(patternResult).isTrue()
        assertThat(matcher.group()).isEqualTo("https://www.hoge.jp/")
        
        val regexResult = target.contains(regex)
        assertThat(regexResult).isTrue()
    }
    
    @Test fun `regexを使ったマッチング処理`() {
        val regex = "https?://[^/]*hoge.jp".toRegex()
        
        // containsは指定した正規表現が含まれるかをチェック
        // Regex.containsMatcheInが使われる
        assertThat("https://hoge.jp/".contains(regex)).isTrue()
        assertThat("https://test.com/hoge.jp/".contains(regex)).isFalse()
        
        // matchesは完全一致、Regex.matchesが使われる
        assertThat("https://hoge.jp".matches(regex)).isTrue()
        assertThat("https://www.hoge.jp/".matches(regex)).isFalse()
    }
    
    @Test fun `ハイフンを含む文字列を含めてregexを作成する`() {
        val data = "tech-blog.rakus.co.jp/"
        val pattern = Pattern.compile("https?://[^/]*$data")
        val regex = "https?://[^/]*$data".toRegex()
        
        val target = "http://tech-blog.rakus.co.jp/entry/20180711/google-apps-script/beginner"
        assertThat(pattern.matcher(target).find()).isTrue()
        assertThat(target.contains(regex)).isTrue()
    }
}
