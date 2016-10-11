package jp.gcreate.sample.daggersandbox.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2016 G-CREATE
 */

public class FilterGenerator {
    private static Pattern DOMAIN = Pattern.compile("http://(.+?/)");
    private static Pattern SUBDIRECTORY = Pattern.compile("http://(.+?/.+?/)");

    public static List<String> generateFilterCandidate(String uri) {
        List<String> candidate = new ArrayList<>();
        String host = getHost(uri);
        candidate.add(host);
        candidate.add(getHostWithoutSubDomain(host));
        candidate.add(getHostAndSubDirectory(uri));
        return candidate;
    }

    public static String getHost(String uri) {
        Matcher m = DOMAIN.matcher(uri);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static String getHostWithoutSubDomain(String uri) {
        String domain = getHost(uri);
        String[] splited = domain.split("\\.");
        if (splited.length <= 2) return domain;
        String ret = splited[splited.length - 2] + "." + splited[splited.length - 1];
        for (int i = splited.length - 3; i > 0; i--) {
            ret = splited[i] + "." + ret;
        }
        return ret;
    }

    public static String getHostAndSubDirectory(String uri) {
        Matcher m = SUBDIRECTORY.matcher(uri);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static String removeSubDomain(String domain) {
        return "";
    }
}
