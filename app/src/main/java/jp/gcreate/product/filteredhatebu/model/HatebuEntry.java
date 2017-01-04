package jp.gcreate.product.filteredhatebu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuEntry {
    @SerializedName("title")
    String title;
    @SerializedName("count")
    int    count;
    @SerializedName("url")
    String url;
    @SerializedName("entry_url")
    String entryUrl;
    @SerializedName("screenshot")
    String screenshotUrl;
    @SerializedName("eid")
    int    entryId;
    @SerializedName("bookmarks")
    List<HatebuBookmark>     bookmarks;
    @SerializedName("related")
    List<HatebuRelatedEntry> related;

    public HatebuEntry(String title, int count, String url, String entryUrl,
                       String screenshotUrl, int entryId,
                       List<HatebuBookmark> bookmarks,
                       List<HatebuRelatedEntry> related) {
        this.title = title;
        this.count = count;
        this.url = url;
        this.entryUrl = entryUrl;
        this.screenshotUrl = screenshotUrl;
        this.entryId = entryId;
        this.bookmarks = bookmarks;
        this.related = related;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEntryUrl() {
        return entryUrl;
    }

    public void setEntryUrl(String entryUrl) {
        this.entryUrl = entryUrl;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public List<HatebuBookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<HatebuBookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public List<HatebuRelatedEntry> getRelated() {
        return related;
    }

    public void setRelated(List<HatebuRelatedEntry> related) {
        this.related = related;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ":" + hashCode() + "){"
               + "title:" + title + ","
               + "count:" + count + ","
               + "url:" + url + ","
               + "entryUrl:" + entryUrl + ","
               + "screenshot:" + screenshotUrl + ","
               + "id:" + entryId + ","
               + "bookmarks:" + bookmarks + ","
               + "related:" + related + "}";
    }
}
