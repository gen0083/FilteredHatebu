package jp.gcreate.product.filteredhatebu.ui.common;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

@RunWith(AndroidJUnit4.class)
public class StringUtilDateTimeTest {
    private Clock   now     = Clock.fixed(Instant.parse("2016-10-19T10:11:12.00Z"), ZoneId.of("Z"));
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void show_2hours_before_no_offset() {
        String actual = StringUtil.whenPublished("2016-10-19T08:11:12", context, now);
        assertThat(actual, is("2時間前"));
    }

    @Test
    public void show_2hours_before_with_offset() {
        String actual = StringUtil.whenPublished("2016-10-19T08:11:12+09:00", context, now);
        assertThat(actual, is("2時間前"));
    }

    @Test
    public void 日付をまたいでいるが24時間は経過していない() {
        String actual = StringUtil.whenPublished("2016-10-18T20:11:12", context, now);
        assertThat(actual, is("14時間前"));
    }

    @Test
    public void 現在時刻より24時間より前の日付なら日付を表示する() {
        String actual = StringUtil.whenPublished("2016-10-17T09:10:12", context, now);
        assertThat(actual, is("2016-10-17"));
    }
}
