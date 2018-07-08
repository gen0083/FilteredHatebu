package jp.gcreate.product.filteredhatebu;

import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class ThreeTenAbpTest {
    private static final String TIME_STRINGS = "2016-10-19T09:19:23+09:00";

    @Test
    public void convert_zoned_date_time() {
        ZonedDateTime time = ZonedDateTime.parse(TIME_STRINGS);
        System.out.println(time);
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        System.out.println(zdt);
    }

    @Test
    public void convert_date_time() {
        LocalDateTime time = LocalDateTime.parse(TIME_STRINGS, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(time);
        assertThat(time.toString()).isEqualTo(("2016-10-19T09:19:23"));
    }

    @Test(expected = DateTimeParseException.class)
    public void convert_no_offset_time() {
        LocalDateTime time = LocalDateTime.parse("2016-10-19T09:10:12", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(time);
        assertThat(time.toString()).isEqualTo("2016-10-19T09:10:12");
    }

    @Test
    public void compute_delta_time() {
        LocalDateTime time = LocalDateTime.parse(TIME_STRINGS, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        LocalDateTime now = LocalDateTime.of(2016, 10, 19, 12, 19, 23);
        long diff = ChronoUnit.HOURS.between(time, now);
        assertThat(diff).isEqualTo(3L);

//        diff = ChronoUnit.HOURS.between(now, time);
//        assertThat(diff, is(3L)); >> この場合-3Lになる。第一引数を基準に第2引数までどれだけ時間の差があるかを算出する
//        第一引数を過去の時刻にしないとマイナスになる
    }

    @Test
    public void 日付が1日以上離れている場合() {
        LocalDateTime time = LocalDateTime.of(2016, 10, 19, 10, 11, 12);
        LocalDateTime now  = LocalDateTime.of(2016, 10, 23, 10, 11, 12);
        long diff = ChronoUnit.HOURS.between(time, now);
        assertThat(diff).isEqualTo(96L);
        diff = ChronoUnit.DAYS.between(time, now);
        assertThat(diff).isEqualTo(4L);
    }

    @Test
    public void 日付の差に誤差が含まれる場合() {
        LocalDateTime time = LocalDateTime.of(2016, 10, 19, 0, 10, 20);
        LocalDateTime now  = LocalDateTime.of(2016, 10, 19, 5,  0, 20);
        long diff = ChronoUnit.HOURS.between(time, now);
        assertThat(diff).isEqualTo(4L);
        // この場合、5時間には10分足りないので4時間が返ってくる(端数は切り捨てられる)
    }

    @Test
    public void yearの下2桁を取得する() {
        ZonedDateTime time = ZonedDateTime.of(2018, 4, 5, 10, 11, 12, 0, ZoneId.of("Asia/Tokyo"));
        String year = time.format(DateTimeFormatter.ofPattern("yy"));
        int month = time.getMonthValue();
        assertThat(year).isEqualTo("18");
        assertThat(month).isEqualTo(4);
    }

    @Test
    public void zonedDateTime_to_long() {
        ZonedDateTime time = ZonedDateTime.of(2018, 4, 5, 10, 11, 12, 0, ZoneId.of("Asia/Tokyo"));
        ZonedDateTime zone = time.truncatedTo(ChronoUnit.SECONDS);
        long longFromZone = zone.toInstant().toEpochMilli();

        LocalDateTime local = LocalDateTime.of(2018, 4, 5, 10, 11, 12);
        long longFromLocal = local.toInstant(ZoneOffset.ofHours(9)).toEpochMilli();
        assertThat(longFromZone).isEqualTo(longFromLocal);
    }

    @Test
    public void zonedDateTime異なるタイムゾーンの表示() {
        ZonedDateTime jst = ZonedDateTime.of(2018, 4, 5, 10, 11, 12, 0, ZoneId.of("Asia/Tokyo"));
        ZonedDateTime utcInstant = jst.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime utcLocale = jst.withZoneSameLocal(ZoneOffset.UTC);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss", Locale.JAPAN);
        System.out.println(fmt.format(jst) + " / " + fmt.format(utcInstant) + " / " + fmt.format(utcLocale));
    }
}
