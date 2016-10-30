package jp.gcreate.product.filteredhatebu.data;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import jp.gcreate.product.filteredhatebu.model.OrmaDatabase;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 19, manifest = Config.NONE)
public class FilterDataSourceOrmaTest {
    private static final String TEST_DB = "unit_test.db";
    private static FilterDataSourceOrma sut;

    @BeforeClass
    public static void initializeTest() {
    }

    @Before
    public void setUp() {
        Context conetxt = RuntimeEnvironment.application;
        conetxt.deleteDatabase(TEST_DB);
        OrmaDatabase orma = OrmaDatabase.builder(conetxt)
                                        .name(TEST_DB)
                                        .build();
        sut = new FilterDataSourceOrma(orma);
    }

    @After
    public void tearDown() {
        RuntimeEnvironment.application.deleteDatabase(TEST_DB);
    }

    @Test
    public void insertFilter() throws Exception {
        TestSubscriber<UriFilter> test = new TestSubscriber<>();
        sut.insertFilter("test.com/");
        sut.getFilter("test.com/")
           .subscribe(test);
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        assertThat(test.getOnNextEvents().get(0).getFilter(), is("test.com/"));
    }

    @Test
    public void getFilterAll() throws Exception {
        TestSubscriber<List<UriFilter>> test = new TestSubscriber<>();
        registerInitialData();
        sut.getFilterAll()
           .subscribe(test);
        test.awaitTerminalEvent();
        test.assertNoErrors();
        List<UriFilter> actual = test.getOnNextEvents().get(0);
        assertThat(actual.get(0).getFilter(), is("test.com/"));
        assertThat(actual.get(1).getFilter(), is("hoge.com/"));
        assertThat(actual.get(2).getFilter(), is("abc.co.jp/"));
    }

    @Test
    public void getFilterAll_noDataCase() throws InterruptedException {
        TestSubscriber<List<UriFilter>> test = new TestSubscriber<>();
        sut.getFilterAll()
           .subscribe(test);
        test.awaitTerminalEvent();
        assertThat(test.getOnNextEvents().size(), is(1));
        List<UriFilter> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(0));
    }

    @Test
    public void getFilter_existCase() throws Exception {
        TestSubscriber<UriFilter> test = new TestSubscriber<>();
        registerInitialData();
        sut.getFilter("abc.co.jp/")
           .subscribe(test);
        test.awaitTerminalEvent();
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        assertThat(test.getOnNextEvents().get(0).getFilter(), is("abc.co.jp/"));
    }

    @Test
    public void getFilter_notExistCase() throws InterruptedException {
        TestSubscriber<UriFilter> test = new TestSubscriber<>();
        registerInitialData();
        sut.getFilter("xyz.com/")
           .subscribe(test);
        test.awaitTerminalEvent(2, TimeUnit.SECONDS);
        test.assertError(NoSuchElementException.class);
    }

    @Test
    public void updateFilter() throws Exception {
        TestSubscriber<List<UriFilter>> test = new TestSubscriber<>();
        registerInitialData();
        sut.updateFilter("hoge.com/", "twitter.com/");
        sut.getFilterAll()
           .subscribe(test);
        test.awaitTerminalEvent(2, TimeUnit.SECONDS);
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        List<UriFilter> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(3));
        assertThat(actual.get(0).getFilter(), is("test.com/"));
        assertThat(actual.get(1).getFilter(), is("twitter.com/"));
        assertThat(actual.get(2).getFilter(), is("abc.co.jp/"));
    }

    @Test
    public void deleteFilter() throws Exception {
        TestSubscriber<List<UriFilter>> test = new TestSubscriber<>();
        registerInitialData();
        sut.deleteFilter("abc.co.jp/");
        sut.getFilterAll()
           .subscribe(test);
        test.awaitTerminalEvent(2, TimeUnit.SECONDS);
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        List<UriFilter> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0).getFilter(), is("test.com/"));
        assertThat(actual.get(1).getFilter(), is("hoge.com/"));
    }

    private void registerInitialData() {
        sut.insertFilter("test.com/");
        sut.insertFilter("hoge.com/");
        sut.insertFilter("abc.co.jp/");
    }

}