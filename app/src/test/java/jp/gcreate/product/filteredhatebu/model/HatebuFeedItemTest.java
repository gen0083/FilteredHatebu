package jp.gcreate.product.filteredhatebu.model;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */
public class HatebuFeedItemTest {
    @Test
    public void equals_same_case() throws Exception {
        HatebuFeedItem a = new HatebuFeedItem();
        HatebuFeedItem b = new HatebuFeedItem();
        // field about is required
        a.setAbout("a");
        b.setAbout("a");
        // field title is required
        a.setTitle("a");
        b.setTitle("a");
        // field link is required
        a.setLink("a");
        b.setLink("a");
        // field description is not required
        a.setDescription("a");
        b.setDescription("a");
        // field encoded is required
        a.setEncoded("a");
        b.setEncoded("a");
        // field date is required
        a.setDate("a");
        b.setDate("a");
        // field subject is not required
        a.setSubject("a");
        b.setSubject("a");
        // field count is required
        a.setCount(1);
        b.setCount(1);

        assertThat(a.equals(b), is(true));
    }

    @Test
    public void equals_same_case_with_null() throws Exception {
        HatebuFeedItem a = new HatebuFeedItem();
        HatebuFeedItem b = new HatebuFeedItem();
        // field about is required
        a.setAbout("a");
        b.setAbout("a");
        // field title is required
        a.setTitle("a");
        b.setTitle("a");
        // field link is required
        a.setLink("a");
        b.setLink("a");
        // field description is not required
        a.setDescription(null);
        b.setDescription(null);
        // field encoded is required
        a.setEncoded("a");
        b.setEncoded("a");
        // field date is required
        a.setDate("a");
        b.setDate("a");
        // field subject is not required
        a.setSubject(null);
        b.setSubject(null);
        // field count is required
        a.setCount(1);
        b.setCount(1);

        assertThat(a.equals(b), is(true));
    }

    @Test
    public void equals_different_case() throws Exception {
        HatebuFeedItem a = new HatebuFeedItem();
        HatebuFeedItem b = new HatebuFeedItem();
        // field about is required
        a.setAbout("a");
        b.setAbout("a");
        // field title is required
        a.setTitle("a");
        b.setTitle("a");
        // field link is required
        a.setLink("a");
        b.setLink("a");
        // field description is not required
        a.setDescription("a");
        b.setDescription("a");
        // field encoded is required
        a.setEncoded("a");
        b.setEncoded("ab");
        // field date is required
        a.setDate("a");
        b.setDate("a");
        // field subject is not required
        a.setSubject("a");
        b.setSubject("a");
        // field count is required
        a.setCount(1);
        b.setCount(1);

        assertThat(a.equals(b), is(false));
    }

    @Test
    public void equals_different_case_with_null() throws Exception {
        HatebuFeedItem a = new HatebuFeedItem();
        HatebuFeedItem b = new HatebuFeedItem();
        // field about is required
        a.setAbout("a");
        b.setAbout("a");
        // field title is required
        a.setTitle("a");
        b.setTitle("a");
        // field link is required
        a.setLink("a");
        b.setLink("a");
        // field description is not required
        a.setDescription(null);
        b.setDescription("a");
        // field encoded is required
        a.setEncoded("a");
        b.setEncoded("a");
        // field date is required
        a.setDate("a");
        b.setDate("a");
        // field subject is not required
        a.setSubject("a");
        b.setSubject("a");
        // field count is required
        a.setCount(1);
        b.setCount(1);

        assertThat(a.equals(b), is(false));
    }

    @Test
    public void decode() throws Exception {
        Serializer serializer = new Persister();
        File file = new File(getClass().getClassLoader().getResource("mock_hatebu_hotentry.rss").getFile());
        HatebuFeed feed = serializer.read(HatebuFeed.class, file);
        HatebuFeedItem actual = feed.getItemList().get(0);
        assertThat(actual.getTitle(), is("test0-1"));
        assertThat(actual.getLink(), is("http://test.com/hoge"));
    }
}