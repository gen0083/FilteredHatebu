package jp.gcreate.product.filteredhatebu.ui.common

import android.databinding.BindingAdapter
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.graphics.drawable.Drawable
import android.widget.ImageButton

@BindingMethods(
    BindingMethod(
        type = ImageButton::class,
        attribute = "srcCompat",
        method = "setSrcCompat"
    )
)
object DatabindingAdapters {
    
    @BindingAdapter(value = ["srcCompat"]) @JvmStatic
    fun setSrcCompat(imageButton: ImageButton, drawable: Drawable) {
        imageButton.setImageDrawable(drawable)
    }
}

