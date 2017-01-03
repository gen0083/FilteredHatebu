package jp.gcreate.product.filteredhatebu.ui.feedlist;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.gcreate.product.filteredhatebu.data.FilterDataSource;
import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.domain.usecase.GetFilteredFeedList;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Copyright 2017 G-CREATE
 */
public class HatebuFeedFragmentPresenterTest {
    private HatebuFeedFragmentPresenter sut;
    @Mock
    GetFilteredFeedList getFilteredFeedList;
    private FilterRepository filterRepository;
    @Mock
    FilterDataSource filterDataSource;
    @Mock
    FaviconUtil      faviconUtil;

    @Before
    public void setUp() {
        initMocks(this);
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        filterRepository = new FilterRepository(filterDataSource);
        sut = new HatebuFeedFragmentPresenter("", getFilteredFeedList, filterRepository, faviconUtil);
        List<HatebuFeedItem> list = new ArrayList<>();
        list.add(new HatebuFeedItem());
        when(getFilteredFeedList.getFilteredList(anyString())).thenReturn(Observable.just(list));
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void フィルタを追加したらフィード一覧が更新される() {
        sut.addFilter("test");
        verify(getFilteredFeedList, timeout(TimeUnit.SECONDS.toMillis(5))).getFilteredList(anyString());
    }

    @Test
    public void フィルタを削除したらフィード一覧が更新される() {
        sut.initializeFilter();
        verify(getFilteredFeedList, timeout(TimeUnit.SECONDS.toMillis(5))).getFilteredList(anyString());
    }

}