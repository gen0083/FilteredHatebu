package jp.gcreate.product.filteredhatebu.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(
    entities = [FeedData::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverter::class, ZonedDateTimeConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun feedDataDao(): FeedDataDao
}
