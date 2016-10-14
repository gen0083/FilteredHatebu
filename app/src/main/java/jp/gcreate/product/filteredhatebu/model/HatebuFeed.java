package jp.gcreate.product.filteredhatebu.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Copyright 2016 G-CREATE
 */

@Root(strict = false)
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", prefix = "rdf"),
        @Namespace(reference = "http://purl.org/rss/1.0/"),
        @Namespace(reference = "http://purl.org/rss/1.0/modules/content/", prefix = "content"),
        @Namespace(reference = "http://purl.org/rss/1.0/modules/taxonomy/", prefix = "taxo"),
        @Namespace(reference = "http://a9.com/-/spec/opensearchrss/1.0/", prefix = "opensearch"),
        @Namespace(reference = "http://purl.org/dc/elements/1.1/", prefix = "dc"),
        @Namespace(reference = "http://www.hatena.ne.jp/info/xmlns#", prefix = "hatena"),
        @Namespace(reference = "http://search.yahoo.com/mrss", prefix = "media")
})
public class HatebuFeed {
    @Element(name = "channel")
    private HatebuFeedChannel channel;

    @ElementList(entry = "item", inline = true)
    private List<HatebuFeedItem> itemList;

    public HatebuFeedChannel getChannel() {
        return channel;
    }

    public void setChannel(
            HatebuFeedChannel channel) {
        this.channel = channel;
    }

    public List<HatebuFeedItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<HatebuFeedItem> itemList) {
        this.itemList = itemList;
    }
}
