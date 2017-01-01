package jp.gcreate.product.filteredhatebu.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2017 G-CREATE
 */
public class HatebuBookmarkTest {
    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder().create();
    }

    @Test
    public void decode() throws Exception {
        File           file = new File(getClass().getClassLoader().getResource("mock_hatebu_entry.json").getFile());
        JsonReader     reader = new JsonReader(new FileReader(file));
        HatebuEntry entry = gson.fromJson(reader, HatebuEntry.class);
        HatebuBookmark actual = entry.getBookmarks().get(0);
        assertThat(actual.getComment(), is("test"));
        assertThat(actual.getUser(), is("test"));
        reader.close();
    }

}