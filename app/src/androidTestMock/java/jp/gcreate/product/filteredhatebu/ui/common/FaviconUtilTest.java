package jp.gcreate.product.filteredhatebu.ui.common;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
public class FaviconUtilTest {
    private FaviconUtil sut;
    @Inject
    OkHttpClient mockedClient;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getTargetContext();
        sut = new FaviconUtil(mockedClient, context);
    }

    @Test
    public void substringUntilDomainTest_withHttp() {
        String target = "http://test.com/a/b/c";
        String actual = sut.substringUntilDomain(target);
        assertThat(actual, is("http://test.com/"));
    }

    @Test
    public void substringUntilDomain_withHttps() {
        String target = "https://test.abc.jp/a/b/c/d";
        String actual = sut.substringUntilDomain(target);
        assertThat(actual, is("https://test.abc.jp/"));
    }

}