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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import rx.Emitter
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.util.*

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
                val bitmap = BitmapFactory.decodeStream(response.body().byteStream())
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
    
    fun fetchFavicon(url: String): Observable<Drawable> {
        val domain = substringUntilDomain(url)
        return Observable.fromEmitter(Action1<Emitter<Drawable>> { subscriber ->
            val request = Request.Builder()
                .url(FAVICON_URL + domain)
                .build()
            if (memoryCache[domain] != null) {
                subscriber.onNext(memoryCache[domain])
                subscriber.onCompleted()
                return@Action1
            }
            subscriber.onNext(placeHolder)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    subscriber.onError(e)
                }
                
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val bitmap = BitmapFactory
                            .decodeStream(response.body().byteStream())
                        val scaled = Bitmap
                            .createScaledBitmap(bitmap, width, height, false)
                        val drawable = BitmapDrawable(resources, scaled)
                        memoryCache[domain] = drawable
                        subscriber.onNext(drawable)
                        subscriber.onCompleted()
                    } catch (e: Exception) {
                        // faviconが取得できなかった場合placeHolderをそのままFaviconとして扱う
                        // faviconが存在しないかBitmapとして処理できなかいケース
                        // eでログを出すと本当のエラーと紛らわしいのでwにしている
                        Timber.w(e)
                        memoryCache[domain] = placeHolder
                        subscriber.onCompleted()
                    } finally {
                        response.close()
                    }
                }
            })
            
            subscriber.setCancellation { client.newCall(request).cancel() }
        }, Emitter.BackpressureMode.NONE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
    
    private fun substringUntilDomain(url: String): String {
        return url.substring(0, url.indexOf("/", 8) + 1)
    }
    
    companion object {
        const val FAVICON_URL = "https://favicon.hatena.ne.jp/?url="
    }
}
