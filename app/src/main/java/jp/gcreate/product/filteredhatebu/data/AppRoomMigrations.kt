package jp.gcreate.product.filteredhatebu.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_filtered_feed_filteredUrl` ON `filtered_feed` (`filteredUrl`)")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_feed_filter_filter` ON `feed_filter` (`filter`)")
    }

}