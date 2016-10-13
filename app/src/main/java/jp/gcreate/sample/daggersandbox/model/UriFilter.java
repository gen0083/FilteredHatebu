package jp.gcreate.sample.daggersandbox.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Copyright 2016 G-CREATE
 */

public class UriFilter extends RealmObject {
    private static final String HTTP = "http(s)?://[^/]*";
    @PrimaryKey
    private String  filter;
    @Ignore
    private Pattern pattern;

    public UriFilter() {}

    public UriFilter(String filter) {
        this.filter = filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
        pattern = null;
    }

    public String getFilter() {
        return filter;
    }

    public boolean isFilteredUrl(String uri) {
        if (pattern == null) {
            pattern = Pattern.compile(HTTP + filter);
        }
        Matcher m = pattern.matcher(uri);
        return m.find();
    }
}
