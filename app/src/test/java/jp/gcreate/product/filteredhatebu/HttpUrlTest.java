package jp.gcreate.product.filteredhatebu;

import org.junit.Test;

import java.util.List;

import okhttp3.HttpUrl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class HttpUrlTest {
    @Test
    public void parse() {
        HttpUrl url = HttpUrl.parse("http://hoge.com/test/index.html");
        assertThat(url.toString(), is("http://hoge.com/test/index.html"));
    }

    @Test
    public void get_scheme() {
        HttpUrl url = HttpUrl.parse("http://hoge.com/test/index.html");
        assertThat(url.scheme(), is("http"));
        assertThat(url.host(), is("hoge.com"));
        assertThat(url.encodedPath(), is("test/index.html"));
    }

    @Test
    public void path_segment() {
        HttpUrl url = HttpUrl.parse("http://hoge.com/test/index.html");
        List<String> pathSegments = url.pathSegments();
        assertThat(pathSegments.size(), is(2));
        assertThat(pathSegments.get(0), is("test"));
        assertThat(pathSegments.get(1), is("index.html"));
    }
}
