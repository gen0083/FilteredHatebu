package jp.gcreate.product.filteredhatebu.ui.editfilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import jp.gcreate.product.filteredhatebu.data.FilterRepository;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import rx.Observable;
import rx.Single;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Copyright 2016 G-CREATE
 */
@RunWith(RobolectricTestRunner.class)
public class FilterEditPresenterTest {
    private FilterEditPresenter sut;
    @Mock
    FilterRepository filterRepository;
    @Mock
    FilterEditContract.View view;

    @Before
    public void setUp() {
        initMocks(this);
        List<UriFilter> initialList = new ArrayList<>();
        initialList.add(new UriFilter("abc.com/"));
        initialList.add(new UriFilter("test.com/"));
        initialList.add(new UriFilter("hoge.com/"));
        when(filterRepository.getFilterAll()).thenReturn(Single.just(initialList));
        when(filterRepository.listenModified()).thenReturn(Observable.<Long>empty());
        sut = new FilterEditPresenter(filterRepository);
        sut.onAttach(view);
    }

    @Test
    public void delete_second_then_is_deleted_position_2_return_true() {
        sut.delete(1);
        assertThat(sut.isDeleted(1), is(true));
    }

    @Test
    public void delete_second_and_first_then_second_one_was_removed_from_list() {
        sut.delete(1);
        sut.delete(0);
        assertThat(sut.getList().size(), is(2));
        assertThat(sut.getList().get(0).getFilter(), is("abc.com/"));
        assertThat(sut.getList().get(1).getFilter(), is("hoge.com/"));
    }

    @Test
    public void delete_second_and_undo_then_list_has_3_items() {
        sut.delete(1);
        sut.undoDelete();
        assertThat(sut.getList().size(), is(3));
    }

    @Test
    public void delete_second_and_third_then_second_one_was_removed_from_list() {
        sut.delete(1);
        sut.delete(2);
        assertThat(sut.getList().size(), is(2));
        assertThat(sut.getList().get(0).getFilter(), is("abc.com/"));
        assertThat(sut.getList().get(1).getFilter(), is("hoge.com/"));
    }
}