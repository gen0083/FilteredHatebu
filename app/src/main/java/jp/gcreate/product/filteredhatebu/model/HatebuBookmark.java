package jp.gcreate.product.filteredhatebu.model;

/**
 * Copyright 2016 G-CREATE
 */

public class HatebuBookmark {
    public static final HatebuBookmark EMPTY = new HatebuBookmark("", new String[]{""}, "", "");

    private String   user;
    private String[] tags;
    private String   timestamp;
    private String   comment;

    public HatebuBookmark(String user, String[] tags, String timestamp, String comment) {
        this.user = user;
        this.tags = tags;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
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
