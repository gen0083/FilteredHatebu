package jp.gcreate.product.filteredhatebu.model

import com.squareup.moshi.JsonClass

/**
 * Copyright 2016 G-CREATE
 */
@JsonClass(generateAdapter = true)
data class HatebuBookmark(
    var user: String,
    var tags: Array<String>,
    var timestamp: String,
    var comment: String
) {
    companion object {
        val EMPTY = HatebuBookmark("", arrayOf(""), "", "")
    }
}
