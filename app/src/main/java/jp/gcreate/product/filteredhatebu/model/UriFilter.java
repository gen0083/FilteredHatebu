package jp.gcreate.product.filteredhatebu.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.Getter;
import com.github.gfx.android.orma.annotation.OnConflict;
import com.github.gfx.android.orma.annotation.Setter;
import com.github.gfx.android.orma.annotation.Table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2016 G-CREATE
 */

@Table
public class UriFilter {
    private static final String HTTP = "http(s)?://[^/]*";
    @Column(value = "filter", unique = true, uniqueOnConflict = OnConflict.IGNORE, indexed = true)
    private String  filter;
    private Pattern pattern;

    public UriFilter() {}

    public UriFilter(String filter) {
        this.filter = filter;
    }

    @Setter("filter")
    public void setFilter(String filter) {
        this.filter = filter;
        pattern = null;
    }

    @Getter("filter")
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
