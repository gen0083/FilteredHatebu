package jp.gcreate.product.filteredhatebu.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import okio.BufferedSource;
import okio.Okio;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2017 G-CREATE
 */
public class HatebuBookmarkTest {
    private Moshi moshi;

    @Before
    public void setUp() {
        moshi = new Moshi.Builder().build();
    }

    @Test
    public void decode() throws Exception {
        File           file = new File(getClass().getClassLoader().getResource("mock_hatebu_entry.json").getFile());
        BufferedSource source = Okio.buffer(Okio.source(file));
        JsonAdapter<HatebuEntry> adapter = moshi.adapter(HatebuEntry.class);
        HatebuEntry entry = adapter.fromJson(source);
        HatebuBookmark actual = entry.getBookmarks().get(0);
        assertThat(actual.getComment(), is("test"));
        assertThat(actual.getUser(), is("test"));
        source.close();
    }

}
