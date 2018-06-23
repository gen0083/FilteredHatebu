package jp.gcreate.product.filteredhatebu.ui.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import androidx.core.net.toUri
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import jp.gcreate.product.filteredhatebu.di.qualifier.ApplicationContext
import javax.inject.Inject

/**
 * Copyright 2018 G-CREATE
 */
@AppScope
class CustomTabHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val requestCode = 1
    private val shareIcon by lazy { BitmapUtil.getBitmap(context, R.drawable.ic_share) }
    private val closeIcon by lazy { BitmapUtil.getBitmap(context, R.drawable.ic_arrow_back) }
    
    fun openCustomTab(url: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        val pendingIntent = PendingIntent.getActivity(context, requestCode, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val customTabIntent = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setShowTitle(true)
            .setCloseButtonIcon(closeIcon)
            .setActionButton(shareIcon, context.getString(R.string.share_url), pendingIntent)
            .addMenuItem(context.getString(R.string.share_url), pendingIntent)
            .build();
        customTabIntent.launchUrl(context, url.toUri())
    }
}
