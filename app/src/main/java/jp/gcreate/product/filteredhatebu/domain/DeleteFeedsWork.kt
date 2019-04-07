package jp.gcreate.product.filteredhatebu.domain

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import jp.gcreate.product.filteredhatebu.data.AppRoomDatabase
import jp.gcreate.product.filteredhatebu.data.entities.debug.WorkLog
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

class DeleteFeedsWork(context: Context, params: WorkerParameters)
    : CoroutineWorker(context, params), KoinComponent {
    
    private val appRoomDatabase: AppRoomDatabase by inject()
    
    override suspend fun doWork(): Result {
        deleteArchivedFeeds()
        return Result.success()
    }
    
    private fun deleteArchivedFeeds() {
        // TODO: ここでは10日過ぎた記事は削除するようにしている
        val targetTime = ZonedDateTime.now().minusDays(10L)
        val feeds = appRoomDatabase.feedDataDao().getArchivedFeeds()
            .filter { !it.isFavorite && it.fetchedAt.isBefore(targetTime) }
        if (feeds.size > 0) {
            feeds.forEach {
                appRoomDatabase.feedDataDao().deleteFeed(it)
            }
            log(WorkLog(0, ZonedDateTime.now(),
                "${feeds.size} feeds delete (archived and before $targetTime)"))
        } else {
            log(WorkLog(0, ZonedDateTime.now(), "delete work run but no feeds deleted."))
        }
    }
    
    private fun log(log: WorkLog) {
        appRoomDatabase.workLogDao().insert(log)
    }
    
    companion object {
        @JvmStatic
        fun schedule() {
            val request = PeriodicWorkRequestBuilder<DeleteFeedsWork>(1L, TimeUnit.DAYS)
                .build()
            
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork("delete_feeds", ExistingPeriodicWorkPolicy.KEEP,
                    request)
        }
    }
}