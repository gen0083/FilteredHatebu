package jp.gcreate.sample.daggersandbox.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2016 G-CREATE
 */

public class UriFilter {
    private static final String HTTP = "http(s)?://[^/]*";
    private String display;
    private Pattern pattern;

    public UriFilter(String filter) {
        this.display = filter;
        pattern = Pattern.compile(HTTP + filter);
    }

    public boolean isFilteredUrl(String uri) {
        Matcher m = pattern.matcher(uri);
        return m.find();
    }
}
