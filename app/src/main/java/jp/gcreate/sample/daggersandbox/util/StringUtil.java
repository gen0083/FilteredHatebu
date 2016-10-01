package jp.gcreate.sample.daggersandbox.util;

/**
 * Copyright 2016 G-CREATE
 */

public class StringUtil {
    public static String concatenateStringArray(String[] array) {
        return concatenateStringArray(array, ",");
    }

    public static String concatenateStringArray(String[] array, String divider) {
        StringBuilder builder = new StringBuilder();
        for (String str : array) {
            builder.append(str);
            builder.append(divider);
        }
        int dividerLength = divider.length();
        builder.delete(builder.length() - dividerLength, builder.length());
        return builder.toString();
    }
}
