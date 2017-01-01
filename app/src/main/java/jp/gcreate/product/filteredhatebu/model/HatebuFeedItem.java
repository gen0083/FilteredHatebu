package jp.gcreate.product.filteredhatebu.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 * Copyright 2016 G-CREATE
 * 人気エントリーで配信される1つ1つの記事
 */

@Root(strict = false)
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", prefix = "rdf"),
        @Namespace(reference = "http://purl.org/rss/1.0/modules/content/", prefix = "content"),
        @Namespace(reference = "http://purl.org/dc/elements/1.1/", prefix = "dc"),
        @Namespace(reference = "http://www.hatena.ne.jp/info/xmlns#", prefix = "hatena")
})
public class HatebuFeedItem implements Parcelable {
    @Attribute
    private String about;

    @Element
    private String title;

    @Element
    private String link;

    @Element(required = false)
    private String description;

    @Element(data = true)
    private String encoded;

    @Element
    private String date;

    @Element(required = false)
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HatebuFeedItem)) return false;
        HatebuFeedItem t = (HatebuFeedItem) obj;
        // non required fields these may be null
        boolean isSame = true;
        isSame &= (this.description == null) ? t.getDescription() == null
                                             : this.description.equals(t.getDescription());
        isSame &= (this.subject == null) ? t.getSubject() == null
                                         : this.subject.equals(t.getSubject());

        // required fields
        isSame &= this.about.equals(t.getAbout()) &&
                  this.title.equals(t.getTitle()) &&
                  this.link.equals(t.getLink()) &&
                  this.encoded.equals(t.getEncoded()) &&
                  this.date.equals(t.getDate()) &&
                  this.count == t.getCount();
        return isSame;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.about);
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeString(this.encoded);
        dest.writeString(this.date);
        dest.writeString(this.subject);
        dest.writeInt(this.count);
    }

    public HatebuFeedItem() {
    }

    protected HatebuFeedItem(Parcel in) {
        this.about = in.readString();
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.encoded = in.readString();
        this.date = in.readString();
        this.subject = in.readString();
        this.count = in.readInt();
    }

    public static final Parcelable.Creator<HatebuFeedItem> CREATOR
            = new Parcelable.Creator<HatebuFeedItem>() {
        @Override
        public HatebuFeedItem createFromParcel(Parcel source) {
            return new HatebuFeedItem(source);
        }

        @Override
        public HatebuFeedItem[] newArray(int size) {
            return new HatebuFeedItem[size];
        }
    };
}
