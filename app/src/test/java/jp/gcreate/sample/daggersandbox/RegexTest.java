package jp.gcreate.sample.daggersandbox;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class RegexTest {
    @Test
    public void test() {
        Pattern p = Pattern.compile("test\\.com");
        Matcher m = p.matcher("http://www.test.com/");
        assertThat(m.find(), is(true));
    }

    @Test
    public void パターンを文字列連結() {
        String filter = "test.com";
        Pattern p = Pattern.compile("http(s)?://[^/]*" + filter + "/");
        Matcher m = p.matcher("http://www.test.com/anything");
        assertThat(m.find(), is(true));
        m = p.matcher("http://hoge.com/test.com/");
        assertThat(m.find(), is(false));
        m = p.matcher("http://test.com/abc/");
        assertThat(m.find(), is(true));
    }

    @Test
    public void ドメイン名を抽出する() {
        Pattern p = Pattern.compile("http://(.+?/)");
        Matcher m = p.matcher("http://www.hoge.com/fuga/something");
        if (m.find()) {
            assertThat(m.groupCount(), is(1));
            assertThat(m.group(1), is("www.hoge.com/"));
        }

        m = p.matcher("http://fuga.com/hoge/test/some/any/something");
        m.find();
        assertThat(m.group(1), is("fuga.com/"));
    }

    @Test
    public void ドメイン名_サブディレクトリを抽出する() {
        Pattern p = Pattern.compile("http://(.+?/.+?/)");
        Matcher m = p.matcher("http://hoge.com/test/abc/def/index.html");
        m.find();
        assertThat(m.group(1), is("hoge.com/test/"));
    }

    @Test
    public void サブドメインを消す() {
        String target = "www.hoge.fuga.co.jp/";
        String[] splited = target.split("\\.");
        assertThat(splited.length, is(5));
    }
}
