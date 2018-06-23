package jp.gcreate.product.filteredhatebu.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Copyright 2016 G-CREATE
 */
@JsonClass(generateAdapter = true)
data class HatebuRelatedEntry(
    var title: String,
    var count: Int,
    var url: String?,
    @field:Json(name = "entry_url")
    var entryUrl: String,
    @field:Json(name = "eid")
    var entryId: String
)
