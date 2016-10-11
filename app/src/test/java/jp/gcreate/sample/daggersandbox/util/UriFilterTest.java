package jp.gcreate.sample.daggersandbox.util;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
@RunWith(Enclosed.class)
public class UriFilterTest {
    public static class ドメイン {

        @Test
        public void ドメインが一致する() {
            UriFilter sut = new UriFilter("test.com/");
            boolean actual = sut.isFilteredUrl("http://test.com/hoge/fuga");
            assertThat(actual, is(true));
        }

        @Test
        public void サブドメインが異なってもマッチする() {
            UriFilter sut = new UriFilter("test.com/");
            boolean actual = sut.isFilteredUrl("http://www.test.com/hoge/fuga");
            assertThat(actual, is(true));
        }

        @Test
        public void ドメインではない部分にフィルタと同じ文字列があった場合はfalseになる() {
            UriFilter sut = new UriFilter("test.com/");
            boolean actual = sut.isFilteredUrl("http://hoge.com/test/test.com/");
            assertThat(actual, is(false));
        }
    }

    public static class サブドメイン {

        @Test
        public void サブドメインまで一致() {
            UriFilter sut = new UriFilter("subdomain.hoge.jp/");
            boolean actual = sut.isFilteredUrl("http://subdomain.hoge.jp/some/value");
            assertThat(actual, is(true));
        }

        @Test
        public void ドメインではない部分にフィルタと同じ文字列があった場合はfalseになる() {
            UriFilter sut = new UriFilter("subdomain.hoge.jp");
            boolean actual = sut.isFilteredUrl("http://test.com/subdomain.hoge.jp/value");
            assertThat(actual, is(false));
        }
    }

    public static class サブディレクトリ {
        @Test
        public void サブディレクトリまで含めてマッチ() {
            UriFilter sut = new UriFilter("hoge.jp/test/");
            boolean actual = sut.isFilteredUrl("http://hoge.jp/test/hoge.jp/");
            assertThat(actual, is(true));
        }

        @Test
        public void サブディレクトリだけ異なる() {
            UriFilter sut = new UriFilter("hoge.jp/test");
            boolean actual = sut.isFilteredUrl("http://hoge.jp/tom/hoge.jp/");
            assertThat(actual, is(false));
        }
    }

    public static class Https {
        @Test
        public void httpsでもマッチ() {
            UriFilter sut = new UriFilter("test.com/");
            boolean actual = sut.isFilteredUrl("https://test.com/some/value");
            assertThat(actual, is(true));
        }
    }
}