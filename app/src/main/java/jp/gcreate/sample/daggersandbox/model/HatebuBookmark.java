package jp.gcreate.sample.daggersandbox.model;

import java.util.Date;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuBookmark {
    String   user;
    String[] tags;
    Date     timestamp;
    String   comment;

    public HatebuBookmark(String user, String[] tags, Date timestamp, String comment) {
        this.user = user;
        this.tags = tags;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ":" + hashCode() + "){"
               + "user:" + user + ","
               + "tags:" + tags + ","
               + "timestamp:" + timestamp + ","
               + "comment:" + comment + "}";
    }
}
