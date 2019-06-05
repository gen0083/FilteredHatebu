package jp.gcreate.product.filteredhatebu.ui.common

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods

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

