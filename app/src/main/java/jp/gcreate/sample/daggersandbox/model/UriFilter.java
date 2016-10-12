package jp.gcreate.sample.daggersandbox.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Copyright 2016 G-CREATE
 */

public class UriFilter extends RealmObject {
    private static final String HTTP = "http(s)?://[^/]*";
    private String  filter;
    @Ignore
    private Pattern pattern;

    public UriFilter() {}

    public UriFilter(String filter) {
        this.filter = filter;
        pattern = Pattern.compile(HTTP + filter);
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public boolean isFilteredUrl(String uri) {
        Matcher m = pattern.matcher(uri);
        return m.find();
    }
}
