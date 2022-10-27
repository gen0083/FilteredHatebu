package jp.gcreate.product.filteredhatebu.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jp.gcreate.product.filteredhatebu.data.dao.*
import jp.gcreate.product.filteredhatebu.data.entities.DeletedFeed
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog

@Database(
    entities = [FeedData::class, FilteredFeed::class, FeedFilter::class, DeletedFeed::class,
        WorkLog::class],
    version = 7,
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverter::class, ZonedDateTimeConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun feedDataDao(): FeedDataDao
    abstract fun filteredFeedDao(): FilteredFeedDao
    abstract fun feedFilterDao(): FeedFilterDao
    abstract fun deletedFeedDao(): DeletedFeedDao
    abstract fun workLogDao(): WorkLogDao
}
