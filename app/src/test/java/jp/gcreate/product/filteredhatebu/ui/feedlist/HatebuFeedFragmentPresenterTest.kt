package jp.gcreate.product.filteredhatebu.ui.feedlist

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import jp.gcreate.product.filteredhatebu.data.FilterDataSource
import jp.gcreate.product.filteredhatebu.data.FilterRepository
import jp.gcreate.product.filteredhatebu.domain.usecase.GetFilteredFeedList
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem
import jp.gcreate.product.filteredhatebu.ui.common.FaviconUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers
import java.util.*

/**
 * Copyright 2017 G-CREATE
 */
class HatebuFeedFragmentPresenterTest {

    private lateinit var sut: HatebuFeedFragmentPresenter
    private lateinit var filterRepository: FilterRepository
    @MockK lateinit var getFilteredFeedList: GetFilteredFeedList
    @MockK lateinit var filterDataSource: FilterDataSource
    @MockK lateinit var faviconUtil: FaviconUtil

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler {
                return Schedulers.immediate()
            }
        })
        filterRepository = FilterRepository(filterDataSource)
        sut = HatebuFeedFragmentPresenter("", getFilteredFeedList, filterRepository, faviconUtil)
        val list = ArrayList<HatebuFeedItem>()
        list.add(HatebuFeedItem())
        every { getFilteredFeedList.getFilteredList(any()) }.returns(Observable.just(list))
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun `フィルタを追加したらフィード一覧が更新される`() {
        sut.addFilter("test")
        verify { getFilteredFeedList.getFilteredList(any()) }
        
    }

    @Test
    fun `フィルタを削除したらフィード一覧が更新される`() {
        sut.initializeFilter()
        verify { getFilteredFeedList.getFilteredList(any()) }
    }
}