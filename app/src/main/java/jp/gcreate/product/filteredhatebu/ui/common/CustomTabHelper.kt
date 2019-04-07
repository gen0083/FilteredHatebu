package jp.gcreate.product.filteredhatebu.ui.common

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import jp.gcreate.product.filteredhatebu.R
import timber.log.Timber

/**
 * Copyright 2018 G-CREATE
 */
class CustomTabHelper(private val context: Context) :
    CustomTabsServiceConnection() {
    
    private val requestCode = 1
    private val shareIcon by lazy { BitmapUtil.getBitmap(context, R.drawable.ic_share) }
    private val closeIcon by lazy { BitmapUtil.getBitmap(context, R.drawable.ic_arrow_back) }
    private var client: CustomTabsClient? = null
    private var isConnected: Boolean = false
    private var session: CustomTabsSession? = null
    private val callback: CustomTabsCallback = object : CustomTabsCallback() {
        override fun onRelationshipValidationResult(relation: Int, requestedOrigin: Uri?,
            result: Boolean, extras: Bundle?) {
            Timber.d(
                "callback onRelationshipValidationResult: $relation origin:$requestedOrigin result:$result ($extras)")
            super.onRelationshipValidationResult(relation, requestedOrigin, result, extras)
        }
        
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            val event = when (navigationEvent) {
                1    -> "NAVIGATION_STARTED"
                2    -> "NAVIGATION_FINISHED"
                3    -> "NAVIGATION_FAILED"
                4    -> "NAVIGATION_ABORTED"
                5    -> "TAB_SHOWN"
                6    -> "TAB_HIDDEN"
                else -> "UNKNOWN"
            }
            Timber.d("callback onNavigationEvent: $event ($extras)")
            super.onNavigationEvent(navigationEvent, extras)
        }
        
        override fun extraCallback(callbackName: String?, args: Bundle?) {
            Timber.d(("callback extraCallback: $callbackName args:$args"))
            super.extraCallback(callbackName, args)
        }
        
        override fun onPostMessage(message: String?, extras: Bundle?) {
            Timber.d("callback onPostMessage: $message ($extras)")
            super.onPostMessage(message, extras)
        }
        
        override fun onMessageChannelReady(extras: Bundle?) {
            Timber.d("callback onMessageChannelReady: $extras")
            super.onMessageChannelReady(extras)
        }
    }
    
    fun preFetch(url: String) {
        connectCustomTabsService()
        Timber.d("do prefetch $url")
        session?.mayLaunchUrl(url.toUri(), null, null)
    }
    
    fun openCustomTab(url: String) {
        connectCustomTabsService()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        val pendingIntent = PendingIntent.getActivity(context, requestCode, shareIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val customTabIntent = CustomTabsIntent.Builder(session)
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setShowTitle(true)
            .setCloseButtonIcon(closeIcon)
            .setActionButton(shareIcon, context.getString(R.string.share_url), pendingIntent)
            .addMenuItem(context.getString(R.string.share_url), pendingIntent)
            .build()
        customTabIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabIntent.launchUrl(context, url.toUri())
    }
    
    private fun connectCustomTabsService() {
        if (!isConnected) {
            val isSuccess = CustomTabsClient.bindCustomTabsService(context, "com.android.chrome",
                this)
            Timber.d("connect tab service is $isSuccess")
        }
    }
    
    override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient?) {
        Timber.d("custom tab service connected: componentName:$name client:$client")
        this.client = client
        isConnected = true
        client?.let {
            it.warmup(0)
            session = it.newSession(callback)
        }
    }
    
    override fun onServiceDisconnected(name: ComponentName?) {
        Timber.d("custom tab service disconnected: componentName:$name")
        this.client = null
        this.session = null
        isConnected = false
    }
}
