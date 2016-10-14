package jp.gcreate.product.filteredhatebu.data;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.functions.Action1;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Copyright 2016 G-CREATE
 */
public class FilterDataSourceRealmTest {
    private static RealmConfiguration    config;
    private static FilterDataSourceRealm sut;

    @BeforeClass
    public static void initializeTest() {
        config = new RealmConfiguration.Builder()
                .name("test_realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        sut = new FilterDataSourceRealm(config);
    }

    @Before
    public void setUp() {
        Realm.deleteRealm(config);
    }

    @After
    public void tearDown() {
        Realm.deleteRealm(config);
    }

    @Test
    public void insertFilter() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        sut.insertFilter("test.com/");
        sut.getFilter("test.com/")
           .subscribe(new Action1<UriFilter>() {
               @Override
               public void call(UriFilter uriFilter) {
                   assertThat(uriFilter.getFilter(), is("test.com/"));
                   latch.countDown();
               }
           }, new Action1<Throwable>() {
               @Override
               public void call(Throwable throwable) {
                   fail();
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void getFilterAll() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        registerInitialData();
        sut.getFilterAll()
           .subscribe(new Action1<List<UriFilter>>() {
               @Override
               public void call(List<UriFilter> uriFilters) {
                   assertThat(uriFilters.size(), is(3));
                   assertThat(uriFilters.get(0).getFilter(), is("test.com/"));
                   assertThat(uriFilters.get(1).getFilter(), is("hoge.com/"));
                   assertThat(uriFilters.get(2).getFilter(), is("abc.co.jp/"));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void getFilterAll_noDataCase() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        sut.getFilterAll()
           .subscribe(new Action1<List<UriFilter>>() {
               @Override
               public void call(List<UriFilter> uriFilters) {
                   fail();
                   latch.countDown();
               }
           }, new Action1<Throwable>() {
               @Override
               public void call(Throwable throwable) {
                   assertThat(throwable, instanceOf(NoSuchElementException.class));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void getFilter_existCase() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        registerInitialData();
        sut.getFilter("abc.co.jp/")
           .subscribe(new Action1<UriFilter>() {
               @Override
               public void call(UriFilter uriFilter) {
                   assertThat(uriFilter.getFilter(), is("abc.co.jp/"));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void getFilter_notExistCase() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        registerInitialData();
        sut.getFilter("xyz.com/")
           .subscribe(new Action1<UriFilter>() {
               @Override
               public void call(UriFilter uriFilter) {
                   fail();
                   latch.countDown();
               }
           }, new Action1<Throwable>() {
               @Override
               public void call(Throwable throwable) {
                   assertThat(throwable, instanceOf(NoSuchElementException.class));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void updateFilter() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        registerInitialData();
        sut.updateFilter("hoge.com/", "twitter.com/");
        sut.getFilterAll()
           .subscribe(new Action1<List<UriFilter>>() {
               @Override
               public void call(List<UriFilter> uriFilters) {
                   assertThat(uriFilters.size(), is(3));
                   assertThat(uriFilters.get(0).getFilter(), is("test.com/"));
                   assertThat(uriFilters.get(1).getFilter(), is("abc.co.jp/"));
                   assertThat(uriFilters.get(2).getFilter(), is("twitter.com/"));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void deleteFilter() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        registerInitialData();
        sut.deleteFilter("abc.co.jp/");
        sut.getFilterAll()
           .subscribe(new Action1<List<UriFilter>>() {
               @Override
               public void call(List<UriFilter> uriFilters) {
                   assertThat(uriFilters.size(), is(2));
                   assertThat(uriFilters.get(0).getFilter(), is("test.com/"));
                   assertThat(uriFilters.get(1).getFilter(), is("hoge.com/"));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    private void registerInitialData() {
        sut.insertFilter("test.com/");
        sut.insertFilter("hoge.com/");
        sut.insertFilter("abc.co.jp/");
    }

}