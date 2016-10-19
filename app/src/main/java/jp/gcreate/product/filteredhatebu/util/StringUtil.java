package jp.gcreate.product.filteredhatebu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2016 G-CREATE
 */

public class StringUtil {
    private static final Pattern PROTOCOL = Pattern.compile("^http[s]?://");

    /**
     * Use TextUtils.join()
     * @param array
     * @return
     */
    @Deprecated
    public static String concatenateStringArray(String[] array) {
        return concatenateStringArray(array, ",");
    }

    public static String concatenateStringArray(String[] array, String divider) {
        if (array == null) return "";
        if (array.length == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (String str : array) {
            builder.append(str);
            builder.append(divider);
        }
        int dividerLength = divider.length();
        builder.delete(builder.length() - dividerLength, builder.length());
        return builder.toString();
    }

    public static String cutProtocolFromUrl(String url) {
        if (url == null || url.equals("")) return "";
        Matcher m = PROTOCOL.matcher(url);
        if (m.find()) {
            return m.replaceFirst("");
        } else {
            return url;
        }
    }
}
