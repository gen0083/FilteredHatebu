package jp.gcreate.product.filteredhatebu.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Copyright 2016 G-CREATE
 */

@Root(strict = false)
@Namespace(reference = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", prefix = "rdf")
public class HatebuFeedChannel {
    @Attribute(name = "about")
    private String about;

    @Element
    private String title;

    @Element
    private String link;

    @Element
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + ":" + hashCode() + "]{"
               + "about:" + about + ", "
               + "title:" + title + ", "
               + "link:" + link + ", "
               + "description:" + description + "}";
    }
}
