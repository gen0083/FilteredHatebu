package jp.gcreate.product.filteredhatebu;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;

/**
 * Copyright 2016 G-CREATE
 */

public class UriTest {
    private Uri sut;

    @Before
    public void setUp() {
        String TEST_URI = "http://what.test.com/did/you/get/path?q=test#fragment";
        sut = Uri.parse(TEST_URI);
    }

    @Test
    public void getHost() {
        String actual = sut.getHost();
        assertThat(actual, is("what.test.com"));
    }

    @Test
    public void getFragment() {
        String actual = sut.getFragment();
        assertThat(actual, is("fragment"));
    }

    @Test
    public void getPath() {
        String actual = sut.getPath();
        assertThat(actual, is("/did/you/get/path"));
    }

    @Test
    public void getQuery() {
        String actual = sut.getQuery();
        assertThat(actual, is("q=test"));
    }
}
