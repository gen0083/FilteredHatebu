package jp.gcreate.product.filteredhatebu.ui.common;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import jp.gcreate.product.filteredhatebu.ui.common.StringUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
@RunWith(Enclosed.class)
public class StringUtilTest {

    public static class 文字列連結 {
        @Test
        public void 文字列の結合() {
            String[] target = {"abc", "def"};
            String   actual = StringUtil.concatenateStringArray(target);
            assertThat(actual, is("abc,def"));
        }

        @Test
        public void 結合記号を指定した場合() {
            String[] target = {"abc", "def"};
            String   actual = StringUtil.concatenateStringArray(target, "/");
            assertThat(actual, is("abc/def"));
        }

        @Test
        public void 結合記号が2文字以上の長さの場合() {
            String[] target = {"abc", "def"};
            String   actual = StringUtil.concatenateStringArray(target, "##");
            assertThat(actual, is("abc##def"));
        }

        @Test
        public void リストに要素がない場合() {
            String[] target = {""};
            String   actual = StringUtil.concatenateStringArray(target);
            assertThat(actual, is(""));
        }

        @Test
        public void リストがnullの場合() {
            String actual = StringUtil.concatenateStringArray(null);
            assertThat(actual, is(""));
        }

        @Test
        public void リストサイズが0の場合() {
            String[] target = new String[0];
            String   actual = StringUtil.concatenateStringArray(target);
            assertThat(actual, is(""));
        }
    }

    public static class URLからプロトコルを消す {

        @Test
        public void httpプロトコルを消した文字列を取得する() {
            String actual = StringUtil.cutProtocolFromUrl("http://www.test.com/abc");
            assertThat(actual, is("www.test.com/abc"));
        }

        @Test
        public void httpsを消した文字列を取得する() {
            String actual = StringUtil.cutProtocolFromUrl("https://test.com/");
            assertThat(actual, is("test.com/"));
        }

        @Test
        public void URLではない文字列の場合には渡した文字列を返す() {
            String actual = StringUtil.cutProtocolFromUrl("hoge");
            assertThat(actual, is("hoge"));
        }

        @Test
        public void nullをわたした場合空文字列を返す() {
            String actual = StringUtil.cutProtocolFromUrl(null);
            assertThat(actual, is(""));
        }

        @Test
        public void 空文字をわたした場合空文字を返す() {
            String actual = StringUtil.cutProtocolFromUrl("");
            assertThat(actual, is(""));
        }

        @Test
        public void プロトコルが先頭以外の場所にある場合は削除しない() {
            String actual = StringUtil.cutProtocolFromUrl("hoge.com/http://abc");
            assertThat(actual, is("hoge.com/http://abc"));
        }
    }

}
