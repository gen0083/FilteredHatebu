package jp.gcreate.product.filteredhatebu.di

import androidx.room.Room
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.FeedData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

val koinDataModule = module {
    single {
        val db = Room.inMemoryDatabaseBuilder(androidContext(),
            AppRoomDatabase::class.java)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        prepareDebugData(db)
        db
    }
}

private fun prepareDebugData(database: AppRoomDatabase) = GlobalScope.launch {
    // StickyHeaderの動作確認のため、FetchAtが異なるフィードデータをいくつか登録する
    for (i in 0..10) {
        database.feedDataDao().insertFeed(
            FeedData(url = "https://gcreate.jp/$i", title = "test $i", summary = "summary $i",
                pubDate = ZonedDateTime.of(2018, 1, 2, 3, 4, 5, 6, ZoneId.systemDefault()),
                fetchedAt = ZonedDateTime.of(2018, 1, 2, 3, 4, 5, 6,
                    ZoneId.systemDefault()))
        )
    }
    for (i in 0..5) {
        database.feedDataDao().insertFeed(
            FeedData(url = "https://android.gcreate.jp/$i", title = "test data $i",
                summary = "sum$i",
                pubDate = ZonedDateTime.of(2018, 2, 3, 4, 5, 6, 7, ZoneId.systemDefault()),
                fetchedAt = ZonedDateTime.of(2018, 2, 3, 4, 5, 6, 7,
                    ZoneId.systemDefault()))
        )
    }
}
