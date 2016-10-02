package jp.gcreate.sample.daggersandbox.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 * Copyright 2016 G-CREATE
 */

@Root(strict = false)
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", prefix = "rdf"),
        @Namespace(reference = "http://purl.org/rss/1.0/modules/content/", prefix = "content"),
        @Namespace(reference = "http://purl.org/dc/elements/1.1/", prefix = "dc"),
        @Namespace(reference = "http://www.hatena.ne.jp/info/xmlns#", prefix = "hatena")
})
public class HatebuFeedItem {
    @Attribute
    private String about;

    @Element
    private String title;

    @Element
    private String link;

    @Element(data = true)
    private String encoded;

    @Element
    private String date;

    @Element
    private String subject;

    @Element(name = "bookmarkcount")
    private int count;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + ":" + hashCode() + "]{"
               + "about:" + about + ", "
               + "title:" + title + ", "
               + "link:" + link + ", "
               + "encoded:" + encoded + ", "
               + "date:" + date + ", "
               + "subject:" + subject + ", "
               + "count:" + count + "}";
    }
}
