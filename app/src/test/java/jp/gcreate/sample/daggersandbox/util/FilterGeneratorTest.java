package jp.gcreate.sample.daggersandbox.util;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
public class FilterGeneratorTest {
    private static String TEST_URL              = "http://test.com/user/category/test";
    private static String TEST_SUBDIRECTORY_URL = "http://test.com/subuser/category/test";
    private static String TEST_SUBDOMAIN_URL    = "http://subdomain.test.com/user/category/sub";

    @Test
    public void generateFilterCandidate() throws Exception {
        List<String> actual = FilterGenerator.generateFilterCandidate(TEST_URL);
        assertThat(actual.size(), is(3));
        actual = FilterGenerator.generateFilterCandidate("http://www.fujitsu.co.jp/product/hoge/index.html");
        assertThat(actual.size(), is(3));
    }

    @Test
    public void getHost() throws Exception {
        String actual = FilterGenerator.getHost(TEST_URL);
        assertThat(actual, is("test.com/"));
        actual = FilterGenerator.getHost(TEST_SUBDOMAIN_URL);
        assertThat(actual, is("subdomain.test.com/"));
    }

    @Test
    public void getHostWithoutSubDomain() throws Exception {
        String actual = FilterGenerator.getHostWithoutSubDomain(TEST_SUBDOMAIN_URL);
        assertThat(actual, is("test.com/"));
        actual = FilterGenerator.getHostWithoutSubDomain(TEST_URL);
        assertThat(actual, is("test.com/"));
        actual = FilterGenerator.getHostWithoutSubDomain("http://www.fujitsu.co.jp/product/index.html");
        assertThat(actual, is("fujitsu.co.jp/"));
    }

    @Test
    public void getHostIncludeUser() throws Exception {
        String actual = FilterGenerator.getHostAndSubDirectory(TEST_SUBDIRECTORY_URL);
        assertThat(actual, is("test.com/subuser/"));
        actual = FilterGenerator.getHostAndSubDirectory(TEST_URL);
        assertThat(actual, is("test.com/user/"));
    }

}