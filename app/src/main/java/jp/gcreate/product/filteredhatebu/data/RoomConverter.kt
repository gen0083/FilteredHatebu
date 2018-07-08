package jp.gcreate.product.filteredhatebu.data

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import java.sql.Timestamp

object LocalDateTimeConverter {
    @TypeConverter @JvmStatic
    fun fromTimestamp(timestamp: Timestamp): LocalDateTime {
        return DateTimeUtils.toLocalDateTime(timestamp)
    }
    
    @TypeConverter @JvmStatic
    fun toTimestamp(time: LocalDateTime): Timestamp {
        return DateTimeUtils.toSqlTimestamp(time)
    }
}

object ZonedDateTimeConverter {
    @TypeConverter @JvmStatic
    fun fromEpochsecond(epoch: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneOffset.UTC)
    }
    
    @TypeConverter @JvmStatic
    fun toEpochsecond(time: ZonedDateTime): Long {
        return time.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond()
    }
}