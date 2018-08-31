package jp.gcreate.product.filteredhatebu.domain.services

import jp.gcreate.product.filteredhatebu.api.HatenaClient
import jp.gcreate.product.filteredhatebu.model.HatebuComments
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber

class BookmarkCommentsService(private val client: HatenaClient.JsonService) {
    suspend fun fetchComments(url: String): HatebuComments {
        return withContext(CommonPool) {
            try {
                val response = client.getHatebuEntry(url).execute()
                if (!response.isSuccessful) {
                    Timber.e(response.errorBody().string())
                    return@withContext HatebuComments.Empty
                }
                val hatebuEntry = response.body()
                val count = hatebuEntry.count
                val bookmarks = hatebuEntry.bookmarks
                if (count > 0 && bookmarks.isEmpty()) {
                    // probably comments disallow
                    Timber.d("bookmark count $count but bookmarks is empty, probably comments disallow")
                    return@withContext HatebuComments.Disallow
                }
                val comments = bookmarks.filter { it.comment.isNotEmpty() }
                return@withContext if (comments.isEmpty()) {
                    Timber.d("comments is empty")
                    HatebuComments.Empty
                } else {
                    Timber.d("get comments ${comments.size}")
                    HatebuComments.Comments(comments)
                }
            } catch (e: Exception) {
                Timber.d("catch exception $e")
                Timber.e(e)
                return@withContext HatebuComments.Error(e)
            }
        }
    }
}
