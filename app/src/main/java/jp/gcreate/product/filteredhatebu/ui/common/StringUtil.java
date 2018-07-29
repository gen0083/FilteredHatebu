package jp.gcreate.product.filteredhatebu.ui.common;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gcreate.product.filteredhatebu.R;

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

    public static String whenPublished(String time, Context context) {
        return whenPublished(time, context, Clock.systemDefaultZone());
    }

    public static String whenPublished(ZonedDateTime time, Context context) {
        ZonedDateTime now = ZonedDateTime.now();
        long diff = ChronoUnit.HOURS.between(time, now); // timeからnowまで何時間の差があるかを調べる、逆にするとマイナスになる
        if (diff > 24) {
            return time.format(DateTimeFormatter.ISO_LOCAL_DATE
                                       .withZone(ZoneOffset.systemDefault()));
        } else if (diff == 0) {
            long minute = ChronoUnit.MINUTES.between(time, now);
            return context.getString(R.string.before_minutes, minute);
        } else {
            return context.getString(R.string.before_hours, diff);
        }
    }

    @VisibleForTesting
    static String whenPublished(String time, Context context, Clock clock) {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                dateTime = LocalDateTime.parse(time);
            } catch (DateTimeParseException e2) {
                dateTime = null;
            }
        }
        if (dateTime == null) return "";
        LocalDateTime now = LocalDateTime.now(clock);
        long diff = ChronoUnit.HOURS.between(dateTime, now);
        if (diff > 24) {
            return dateTime.toLocalDate().toString();
        } else {
            return context.getResources().getString(R.string.before_hours, diff);
        }
    }
}
