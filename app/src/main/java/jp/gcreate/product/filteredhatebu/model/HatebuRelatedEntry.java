package jp.gcreate.product.filteredhatebu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuRelatedEntry {
    String title;
    int    count;
    String url;
    @SerializedName("entry_url")
    String entryUrl;
    @SerializedName("eid")
    int    entryId;

    public HatebuRelatedEntry(String title, int count, String url, String entryUrl, int entryId) {
        this.title = title;
        this.count = count;
        this.url = url;
        this.entryUrl = entryUrl;
        this.entryId = entryId;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ":" + hashCode() + "){"
               + "title:" + title + ","
               + "count:" + count + ","
               + "url:" + url + ","
               + "entryUrl" + entryUrl + ","
               + "id:" + entryId + "}";
    }
}
