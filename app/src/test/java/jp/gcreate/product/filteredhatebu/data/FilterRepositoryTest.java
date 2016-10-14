package jp.gcreate.product.filteredhatebu.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
public class FilterRepositoryTest {
    private FilterRepository sut;
    private List<UriFilter>  source;
    private FilterDataSource testSource;

    @Before
    public void setUp() {
        source = new ArrayList<>();
        source.add(new UriFilter("test.com/"));
        source.add(new UriFilter("hoge.com/"));
        source.add(new UriFilter("abc.co.jp/"));

        testSource = new FilterDataSource() {
            @Override
            public void insertFilter(String insert) {
                source.add(new UriFilter(insert));
            }

            @Override
            public Single<List<UriFilter>> getFilterAll() {
                return Single.just(source);
            }

            @Override
            public Single<UriFilter> getFilter(final String filter) {
                return Observable.from(source)
                                 .filter(new Func1<UriFilter, Boolean>() {
                                     @Override
                                     public Boolean call(UriFilter uriFilter) {
                                         return uriFilter.getFilter().equals(filter);
                                     }
                                 })
                                 .first()
                                 .toSingle();
            }

            @Override
            public void updateFilter(String old, String update) {
                for (int i = 0; i < source.size(); i++) {
                    if (source.get(i).getFilter().equals(old)) {
                        source.get(i).setFilter(update);
                        return;
                    }
                }
            }

            @Override
            public void deleteFilter(String delete) {
                for (int i = 0; i < source.size(); i++) {
                    if (source.get(i).getFilter().equals(delete)) {
                        source.remove(i);
                        return;
                    }
                }
            }
        };
        sut = new FilterRepository(testSource);
    }

    @Test
    public void insertFilter() throws Exception {
        UriFilter target = new UriFilter("new.com/");
        sut.insertFilter(target.getFilter());
        assertThat(source.size(), is(4));
        assertThat(source.get(3).getFilter(), is("new.com/"));
    }

    @Test
    public void getFilterAll() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        sut.getFilterAll()
           .subscribe(new Action1<List<UriFilter>>() {
               @Override
               public void call(List<UriFilter> uriFilters) {
                   assertThat(uriFilters.size(), is(3));
                   assertThat(uriFilters.get(0).getFilter(), is("test.com/"));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void getFilterWithId() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        sut.getFilter("hoge.com/")
           .subscribe(new Action1<UriFilter>() {
               @Override
               public void call(UriFilter uriFilter) {
                   assertThat(uriFilter.getFilter(), is("hoge.com/"));
                   latch.countDown();
               }
           });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void updateFilter() throws Exception {
        UriFilter target = new UriFilter("update.com/");
        sut.updateFilter("hoge.com/", target.getFilter());
        assertThat(source.get(1).getFilter(), is("update.com/"));
    }

    @Test
    public void deleteFilter() throws Exception {
        sut.deleteFilter("hoge.com/");
        assertThat(source.size(), is(2));
        assertThat(source.get(1).getFilter(), is("abc.co.jp/"));
    }

}