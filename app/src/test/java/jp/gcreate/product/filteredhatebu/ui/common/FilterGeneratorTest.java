package jp.gcreate.product.filteredhatebu.ui.common;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import jp.gcreate.product.filteredhatebu.ui.common.FilterGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
@RunWith(Enclosed.class)
public class FilterGeneratorTest {
    public static class フィルタ候補生成 {
        @Test
        public void ホスト部2つサブディレクトリあり() {
            List<String> actual = FilterGenerator
                    .generateFilterCandidate("http://test.com/user/category/test");
            assertThat(actual.size(), is(2));
            assertThat(actual.get(0), is("test.com/"));
            assertThat(actual.get(1), is("test.com/user/"));
        }

        @Test
        public void ホスト部4つサブディレクトリあり() {
            List<String> actual = FilterGenerator.generateFilterCandidate("http://www.fujitsu.co.jp/product/hoge/index.html");
            assertThat(actual.size(), is(4));
            assertThat(actual.get(0), is("www.fujitsu.co.jp/"));
            assertThat(actual.get(1), is("fujitsu.co.jp/"));
            assertThat(actual.get(2), is("www.fujitsu.co.jp/product/"));
            assertThat(actual.get(3), is("fujitsu.co.jp/product/"));

        }

        @Test
        public void ホスト部2つサブディレクトリなし() {
            List<String> actual = FilterGenerator.generateFilterCandidate("http://hoge.com/abcde1234");
            assertThat(actual.size(), is(1));
            assertThat(actual.get(0), is("hoge.com/"));
        }

        @Test
        public void ホスト部4つサブディレクトリなし() {
            List<String> actual = FilterGenerator.generateFilterCandidate("http://a.b.c.d/hoge");
            assertThat(actual.size(), is(2));
            assertThat(actual.get(0), is("a.b.c.d/"));
            assertThat(actual.get(1), is("b.c.d/"));
        }

    }


    public static class ホスト部分 {
        @Test
        public void ホスト部2つ() {
            String actual = FilterGenerator.getHost("http://test.com/hoge/fuga");
            assertThat(actual, is("test.com/"));
        }

        @Test
        public void ホスト部3つ() {
            String actual = FilterGenerator.getHost("http://subdomain.test.com/hoge/fuga");
            assertThat(actual, is("subdomain.test.com/"));
        }

        @Test
        public void ホスト部1つ() {
            String actual = FilterGenerator.getHost("http://test/");
            assertThat(actual, is("test/"));
        }

        @Test
        public void https() {
            String actual = FilterGenerator.getHost("https://hoge.com/");
            assertThat(actual, is("hoge.com/"));
        }
    }

    public static class サブドメインを除去 {

        @Test
        public void ホスト部が2つ() {
            String actual = FilterGenerator.getHostWithoutSubDomain("http://test.com/fuga");
            assertThat(actual, is("test.com/"));
        }

        @Test
        public void ホスト部が3つ() {
            String actual = FilterGenerator.getHostWithoutSubDomain("http://hoge.fuga.com/fuga");
            assertThat(actual, is("fuga.com/"));
        }

        @Test
        public void ホスト部が4つ() {
            String actual = FilterGenerator.getHostWithoutSubDomain("http://www.fujitsu.co.jp/product/1234");
            assertThat(actual, is("fujitsu.co.jp/"));
        }

        @Test
        public void https() {
            String actual = FilterGenerator.getHostWithoutSubDomain("https://www.fujitsu.co.jp/product/1234");
            assertThat(actual, is("fujitsu.co.jp/"));
        }
    }

    public static class サブディレクトリ {
        @Test
        public void サブディレクトリあり() {
            String actual = FilterGenerator.getHostAndSubDirectory("http://test.com/subuser/123");
            assertThat(actual, is("test.com/subuser/"));
        }

        @Test
        public void サブディレクトリなし() {
            String actual = FilterGenerator.getHostAndSubDirectory("http://test.com/1234");
            assertThat(actual, is(""));
        }

        @Test
        public void https() {
            String actual = FilterGenerator.getHostAndSubDirectory("https://test.com/1234");
            assertThat(actual, is(""));
        }
    }

    public static class サブドメインの除去メソッド {
        @Test
        public void ホスト部が2つ() {
            String actual = FilterGenerator.removeSubDomain("test.com/");
            assertThat(actual, is("test.com/"));
        }

        @Test
        public void ホスト部が3つ() {
            String actual = FilterGenerator.removeSubDomain("www.abc.com/");
            assertThat(actual, is("abc.com/"));
        }

        @Test
        public void サブディレクトリを含む() {
            String actual = FilterGenerator.removeSubDomain("a.b.c/sub/");
            assertThat(actual, is("b.c/sub/"));
        }
    }
}