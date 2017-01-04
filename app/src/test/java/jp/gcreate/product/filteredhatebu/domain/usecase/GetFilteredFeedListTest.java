package jp.gcreate.product.filteredhatebu.domain.usecase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.api.FeedsBurnerClienet;
import jp.gcreate.product.filteredhatebu.api.HatenaClient;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.model.HatebuFeed;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Copyright 2016 G-CREATE
 */
public class GetFilteredFeedListTest {
    private GetFilteredFeedList sut;
    @Mock
    FeedsBurnerClienet      feedsBurnerClienet;
    @Mock
    HatenaClient.XmlService hatenaXmlService;
    @Mock
    FilterRepository        filterRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        sut = new GetFilteredFeedList(feedsBurnerClienet, hatenaXmlService, filterRepository,
                                      Schedulers.immediate(), Schedulers.immediate());
        ClassLoader loader       = getClass().getClassLoader();
        Serializer  serializer   = new Persister();
        File        feedsBurner  = new File(
                loader.getResource("mock_hatebu_hotentry.rss").getFile());
        HatebuFeed  hotentryList = serializer.read(HatebuFeed.class, feedsBurner);
        when(feedsBurnerClienet.getHotentryFeed()).thenReturn(Observable.just(hotentryList));
        File       hatenaXml    = new File(
                loader.getResource("mock_hatebu_hotentry_category.rss").getFile());
        HatebuFeed categoryList = serializer.read(HatebuFeed.class, hatenaXml);
        when(hatenaXmlService.getCategoryFeed(anyString()))
                .thenReturn(Observable.just(categoryList));
        when(filterRepository.getFilterAll())
                .thenReturn(Single.<List<UriFilter>>just(new ArrayList<UriFilter>()));
    }

    @Test
    public void getFilteredList_nofilters() throws Exception {
        TestSubscriber<List<HatebuFeedItem>> test = TestSubscriber.create();
        sut.getFilteredList("").subscribe(test);
        test.awaitTerminalEvent();
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        List<HatebuFeedItem> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(30));
        assertThat(actual.get(0).getLink(), is("http://test.com/hoge"));
        assertThat(actual.get(2).getLink(), is("http://test.com/test/is/difficult"));
        assertThat(actual.get(6).getLink(), is("http://www.test.com/test/manager"));
    }

    @Test
    public void getFilteredList_filtered() throws Exception {
        List<UriFilter> filterList = new ArrayList<>();
        filterList.add(new UriFilter("test.com/"));
        when(filterRepository.getFilterAll()).thenReturn(Single.just(filterList));
        TestSubscriber<List<HatebuFeedItem>> test = TestSubscriber.create();
        sut.getFilteredList("").subscribe(test);
        test.awaitTerminalEvent();
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        List<HatebuFeedItem> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(27));
        assertThat(actual.get(0).getTitle(), not("http://test.com/hoge"));
    }

    @Test
    public void getFilteredList_filtered_pettern2() throws Exception {
        List<UriFilter> filterList = new ArrayList<>();
        filterList.add(new UriFilter("test.com/test/"));
        when(filterRepository.getFilterAll()).thenReturn(Single.just(filterList));
        TestSubscriber<List<HatebuFeedItem>> test = TestSubscriber.create();
        sut.getFilteredList("").subscribe(test);
        test.awaitTerminalEvent();
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        List<HatebuFeedItem> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(28));
        assertThat(actual.get(0).getLink(), is("http://test.com/hoge"));
        assertThat(actual.get(2).getLink(), not("http://test.com/test/is/difficult"));
    }

    @Test
    public void getFilteredList_as_category() throws Exception {
        TestSubscriber<List<HatebuFeedItem>> test = TestSubscriber.create();
        sut.getFilteredList("it").subscribe(test);
        test.awaitTerminalEvent();
        test.assertNoErrors();
        assertThat(test.getValueCount(), is(1));
        List<HatebuFeedItem> actual = test.getOnNextEvents().get(0);
        assertThat(actual.size(), is(30));
        assertThat(actual.get(0).getLink(), not("http://test.com/hoge"));
        assertThat(actual.get(0).getTitle(), is("category test"));
    }
}