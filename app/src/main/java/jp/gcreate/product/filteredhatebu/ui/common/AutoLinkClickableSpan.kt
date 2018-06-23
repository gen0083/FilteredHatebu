package jp.gcreate.product.filteredhatebu.ui.common

import android.text.style.ClickableSpan
import android.view.View

/**
 * Copyright 2018 G-CREATE
 */
class AutoLinkClickableSpan(val url: String, private val helper: CustomTabHelper) : ClickableSpan() {
    
    override fun onClick(view: View) {
        helper.openCustomTab(url)
    }
}