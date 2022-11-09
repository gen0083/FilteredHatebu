package jp.gcreate.product.filteredhatebu.domain

import android.content.Context
import androidx.work.*
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

class DeleteFeedsWork(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val appRoomDatabase: AppRoomDatabase by inject()

    override suspend fun doWork(): Result {
        // TODO: ここでは10日過ぎた記事は削除するようにしている
        val targetTime = ZonedDateTime.now().minusDays(10L)
        deleteArchivedFeeds(targetTime)
        deleteFilteredFeeds(targetTime)
        return Result.success()
    }

    private suspend fun deleteArchivedFeeds(targetTime: ZonedDateTime) {
        withContext(Dispatchers.IO) {
            val feeds = appRoomDatabase.feedDataDao().getArchivedFeeds()
                .filter { !it.isFavorite && it.fetchedAt.isBefore(targetTime) }
            if (feeds.isNotEmpty()) {
                feeds.forEach {
                    appRoomDatabase.feedDataDao().deleteFeed(it)
                }
                log(
                    WorkLog(
                        0, ZonedDateTime.now(),
                        "${feeds.size} feeds delete (archived and before $targetTime)"
                    )
                )
            } else {
                log(WorkLog(0, ZonedDateTime.now(), "delete work run but no feeds deleted."))
            }
        }
    }

    private suspend fun deleteFilteredFeeds(targetTime: ZonedDateTime) {
        withContext(Dispatchers.IO) {
            val feeds = appRoomDatabase.feedDataDao().getFilteredFeedsByState(false, false)
                .filter { it.fetchedAt.isBefore(targetTime) }
            if (feeds.isNotEmpty()) {
                feeds.forEach {
                    appRoomDatabase.feedDataDao().deleteFeed(it)
                }
                log(
                    WorkLog(
                        0,
                        ZonedDateTime.now(),
                        "${feeds.size} feeds which filtered are deleted"
                    )
                )
            } else {
                log(WorkLog(0, ZonedDateTime.now(), "no filtered feeds are deleted"))
            }
        }
    }

    private suspend fun log(log: WorkLog) {
        appRoomDatabase.workLogDao().insert(log)
    }

    companion object {
        @JvmStatic
        fun schedule() {
            val request = PeriodicWorkRequestBuilder<DeleteFeedsWork>(1L, TimeUnit.DAYS)
                .build()

            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                    "delete_feeds", ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}