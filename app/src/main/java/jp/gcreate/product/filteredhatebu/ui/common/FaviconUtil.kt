package jp.gcreate.product.filteredhatebu.ui.common

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import jp.gcreate.product.filteredhatebu.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber

/**
 * Copyright 2016 G-CREATE
 */
class FaviconUtil(private val client: OkHttpClient, context: Context) {
    
    private val resources: Resources = context.resources
    private val memoryCache = HashMap<String, Drawable>()
    private val placeHolder: Drawable =
        ContextCompat.getDrawable(context, R.drawable.favicon_placeholder)!!
    private val width: Int = placeHolder.intrinsicWidth
    private val height: Int = placeHolder.intrinsicHeight
    
    suspend fun fetchFaviconWithCoroutine(url: String): Drawable {
        val domain = substringUntilDomain(url)
        val drawable = withContext(Dispatchers.Default) {
            memoryCache[domain]?.let { return@withContext it }
            try {
                val response = client.newCall(Request.Builder().url(FAVICON_URL + domain).build())
                    .execute()
                val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                val scaled = Bitmap.createScaledBitmap(bitmap, width, height, false)
                val drawable = BitmapDrawable(resources, scaled)
                memoryCache[domain] = drawable
                return@withContext drawable
            } catch (e: Exception) {
                Timber.e(e)
                return@withContext placeHolder
            }
        }
        return drawable
    }

    private fun substringUntilDomain(url: String): String {
        return url.substring(0, url.indexOf("/", 8) + 1)
    }
    
    companion object {
        const val FAVICON_URL = "https://favicon.hatena.ne.jp/?url="
    }
}
