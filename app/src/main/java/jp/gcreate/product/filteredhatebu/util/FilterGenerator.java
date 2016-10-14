package jp.gcreate.product.filteredhatebu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2016 G-CREATE
 */

public class FilterGenerator {
    private static Pattern DOMAIN = Pattern.compile("http[s]?://(.+?/)");
    private static Pattern SUBDIRECTORY = Pattern.compile("http[s]?://(.+?/.+?/)");

    public static List<String> generateFilterCandidate(String uri) {
        List<String> candidate = new ArrayList<>();
        String host = getHost(uri);
        candidate.add(host);
        String subdomain = getHostWithoutSubDomain(uri);
        boolean isSameDomain = host.equals(subdomain);
        if (!isSameDomain) {
            candidate.add(subdomain);
        }
        String subdirectory = getHostAndSubDirectory(uri);
        if (!subdirectory.equals("")) {
            candidate.add(subdirectory);
            if (!isSameDomain) {
                candidate.add(removeSubDomain(subdirectory));
            }
        }
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
        return removeSubDomain(domain);
    }

    public static String getHostAndSubDirectory(String uri) {
        Matcher m = SUBDIRECTORY.matcher(uri);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static String getHostAndSubDirectoryWithoutSubDomain(String uri) {
        return removeSubDomain(getHostAndSubDirectory(uri));
    }

    static String removeSubDomain(String uri) {
        String[] split = uri.split("/");
        if (split.length >= 1) {
            String host = split[0];
            String[] segment = host.split("\\.");
            if (segment.length > 2) {
                int position = host.indexOf(".") + 1;
                return uri.substring(position, uri.length());
            } else {
                return uri;
            }
        } else {
            throw new RuntimeException("FilterGenerator in removeSubDomain failed. Uri format invalid. Expected xxx.com/");
        }
    }
}
