package jp.gcreate.sample.daggersandbox;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 19, manifest = Config.NONE)
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
    public void getFragmetn() {
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
