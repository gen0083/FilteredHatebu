package jp.gcreate.product.filteredhatebu.ui.common

class HandleOnceEvent<out T>(private val content: T) {
    private var isHandled: Boolean = false
    
    fun handleEvent(): T? {
        return if (!isHandled) {
            isHandled = true
            content
        } else {
            null
        }
    }
}