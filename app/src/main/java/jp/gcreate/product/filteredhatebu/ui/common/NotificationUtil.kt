package jp.gcreate.product.filteredhatebu.ui.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.core.content.systemService
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext
import jp.gcreate.product.filteredhatebu.ui.MainActivity
import timber.log.Timber
import javax.inject.Inject

const val DEFAULT_CHANNEL = "default"
const val DEFAULT_CHANNEL_ID = 1
const val NEW_CHANNEL = "new_channel"
const val NEW_CHANNEL_ID = 10
const val NOTIFICATION_GROUP_NEW = "new_feed_notification_group"

@AppScope
class NotificationUtil @Inject constructor(@ApplicationContext private val context: Context) {
    
    private val manager = NotificationManagerCompat.from(context)
    
    fun installChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultTitle = context.getString(R.string.notification_channel_title_default)
            val channelDefault = NotificationChannel(DEFAULT_CHANNEL,
                                                     defaultTitle,
                                                     NotificationManager.IMPORTANCE_DEFAULT)
            channelDefault.apply {
                description = context.getString(R.string.notification_channel_description_default)
                setShowBadge(false)
                enableVibration(true)
                enableLights(true)
            }
            val newTitle = context.getString(R.string.notification_channel_title_new_feed)
            val newChannel = NotificationChannel(NEW_CHANNEL, newTitle,
                                                 NotificationManager.IMPORTANCE_DEFAULT)
            newChannel.apply {
                description = context.getString(R.string.notification_channel_description_new_feed)
                setShowBadge(true)
                enableVibration(false)
                enableLights(true)
            }
            
            context.systemService<NotificationManager>().let {
                it.createNotificationChannels(listOf(channelDefault, newChannel))
            }
        } else {
            Timber.d("this device dose not support notification channel")
        }
    }
    
    fun notifyNewFeedsCount(count: Int) {
        if (count == 0) {
            Timber.d("new feed=0 and skip to notify")
            return
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        
        val notification = NotificationCompat.Builder(context, NEW_CHANNEL)
            .setSmallIcon(R.drawable.ic_feed)
            .setContentTitle(context.getString(R.string.new_contents_fetched))
            .setContentText(context.getString(R.string.new_contents_count, count))
            .setNumber(count)
            .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP_NEW)
            .build()
        manager.notify(NEW_CHANNEL_ID, notification)
    }
}
