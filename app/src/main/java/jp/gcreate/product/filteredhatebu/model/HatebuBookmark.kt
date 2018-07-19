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
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as HatebuBookmark
        
        if (user != other.user) return false
        if (timestamp != other.timestamp) return false
        if (comment != other.comment) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + comment.hashCode()
        return result
    }
}

sealed class HatebuComments {
    object Disallow: HatebuComments()
    
    data class Comments(val comments: List<HatebuBookmark>): HatebuComments()
    
    object Empty : HatebuComments()
    
    data class Error(val cause: Exception): HatebuComments()
    
    object Loading : HatebuComments()
}
