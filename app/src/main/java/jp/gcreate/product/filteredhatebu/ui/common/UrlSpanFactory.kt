package jp.gcreate.product.filteredhatebu.ui.common

import android.text.Spannable
import android.text.Spanned
import androidx.core.text.toSpannable
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Copyright 2018 G-CREATE
 */
@AppScope
class UrlSpanFactory @Inject constructor(private val helper: CustomTabHelper) : Spannable.Factory() {
    private val regex = Pattern.compile("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
    
    override fun newSpannable(source: CharSequence): Spannable {
        // sourceはsetText内でnullの場合に空白に変換されているのでnon-nullにしても大丈夫だと思う
        val spannable = source.toSpannable()
        val matcher = regex.matcher(source)
        while(matcher.find()) {
            val url = matcher.group()
            val start = matcher.start()
            val end = matcher.end()
            spannable.setSpan(AutoLinkClickableSpan(url, helper), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }
}