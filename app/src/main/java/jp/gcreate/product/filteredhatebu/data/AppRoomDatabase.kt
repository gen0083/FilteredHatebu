package jp.gcreate.product.filteredhatebu.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import jp.gcreate.product.filteredhatebu.data.dao.FeedDataDao
import jp.gcreate.product.filteredhatebu.data.dao.FeedFilterDao
import jp.gcreate.product.filteredhatebu.data.dao.FilteredFeedDao
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import jp.gcreate.product.filteredhatebu.data.entities.FeedFilter
import jp.gcreate.product.filteredhatebu.data.entities.FilteredFeed

@Database(
    entities = [FeedData::class, FilteredFeed::class, FeedFilter::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverter::class, ZonedDateTimeConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun feedDataDao(): FeedDataDao
    abstract fun filteredFeedDao(): FilteredFeedDao
    abstract fun feedFilterDao(): FeedFilterDao
}
