package jp.gcreate.product.filteredhatebu

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

/**
 * Copyright 2016 G-CREATE
 */
class ThreeTenAbpTest {

    @Test fun convert_zoned_date_time() {
        val time = ZonedDateTime.parse(TIME_STRINGS)
        println(time)
        val zdt = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
        println(zdt)
    }
    
    @Test fun convert_date_time() {
        val time = LocalDateTime.parse(TIME_STRINGS, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        println(time)
        assertThat(time.toString()).isEqualTo("2016-10-19T09:19:23")
    }
    
    @Test(expected = DateTimeParseException::class)
    fun convert_no_offset_time() {
        val time = LocalDateTime.parse("2016-10-19T09:10:12",
                                       DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        println(time)
        assertThat(time.toString()).isEqualTo("2016-10-19T09:10:12")
    }
    
    @Test fun compute_delta_time() {
        val time = LocalDateTime.parse(TIME_STRINGS, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val now = LocalDateTime.of(2016, 10, 19, 12, 19, 23)
        val diff = ChronoUnit.HOURS.between(time, now)
        assertThat(diff).isEqualTo(3L)
        //        diff = ChronoUnit.HOURS.between(now, time);
        //        assertThat(diff, is(3L)); >> この場合-3Lになる。第一引数を基準に第2引数までどれだけ時間の差があるかを算出する
        //        第一引数を過去の時刻にしないとマイナスになる
    }
    
    @Test fun `日付が1日以上離れている場合`() {
        val time = LocalDateTime.of(2016, 10, 19, 10, 11, 12)
        val now = LocalDateTime.of(2016, 10, 23, 10, 11, 12)
        var diff = ChronoUnit.HOURS.between(time, now)
        assertThat(diff).isEqualTo(96L)
        diff = ChronoUnit.DAYS.between(time, now)
        assertThat(diff).isEqualTo(4L)
    }
    
    @Test fun `日付の差に誤差が含まれる場合`() {
        val time = LocalDateTime.of(2016, 10, 19, 0, 10, 20)
        val now = LocalDateTime.of(2016, 10, 19, 5, 0, 20)
        val diff = ChronoUnit.HOURS.between(time, now)
        assertThat(diff).isEqualTo(4L)
        // この場合、5時間には10分足りないので4時間が返ってくる(端数は切り捨てられる)
    }
    
    @Test fun `yearの下2桁を取得する`() {
        val time = ZonedDateTime.of(2018, 4, 5, 10, 11, 12, 0, ZoneId.of("Asia/Tokyo"))
        val year = time.format(DateTimeFormatter.ofPattern("yy"))
        val month = time.monthValue
        assertThat(year).isEqualTo("18")
        assertThat(month).isEqualTo(4)
    }
    
    @Test fun zonedDateTime_to_long() {
        val time = ZonedDateTime.of(2018, 4, 5, 10, 11, 12, 0, ZoneId.of("Asia/Tokyo"))
        val zone = time.truncatedTo(ChronoUnit.SECONDS)
        val longFromZone = zone.toInstant().toEpochMilli()
        val local = LocalDateTime.of(2018, 4, 5, 10, 11, 12)
        val longFromLocal = local.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
        assertThat(longFromZone).isEqualTo(longFromLocal)
    }
    
    @Test fun `zonedDateTime異なるタイムゾーンの表示`() {
        val jst = ZonedDateTime.of(2018, 4, 5, 10, 11, 12, 0, ZoneId.of("Asia/Tokyo"))
        val utcInstant = jst.withZoneSameInstant(ZoneOffset.UTC)
        val utcLocale = jst.withZoneSameLocal(ZoneOffset.UTC)
        val fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss", Locale.JAPAN)
        println(fmt.format(jst) + " / " + fmt.format(utcInstant) + " / " + fmt.format(utcLocale))
    }
    
    @Test fun `jstで取得した日付をutcに変換し単純に日付部分を文字列表示すると時差の関係でずれる`() {
        val base = ZonedDateTime.of(2018, 7, 29, 1, 23, 45, 0, ZoneId.of("Asia/Tokyo"))
        val utc = base.withZoneSameInstant(ZoneOffset.UTC)
        assertThat(utc.isEqual(base)).isTrue()
        assertThat(base.toLocalDate().toString()).isEqualTo("2018-07-29")
        assertThat(utc.toLocalDate().toString()).isEqualTo("2018-07-28")
    }

    companion object {
        private const val TIME_STRINGS = "2016-10-19T09:19:23+09:00"
    }
}
