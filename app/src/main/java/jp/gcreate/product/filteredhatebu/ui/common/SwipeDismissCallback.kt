package jp.gcreate.product.filteredhatebu.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import jp.gcreate.product.filteredhatebu.R
import kotlin.math.abs

class SwipeDismissCallback(
    context: Context,
    @DrawableRes
    iconDrawable: Int = R.drawable.ic_archive,
    private val onSwiped: (adapterPosition: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    
    private val icon = ContextCompat.getDrawable(context, iconDrawable)!!.apply {
        setTint(ContextCompat.getColor(context, R.color.colorAccent))
    }
    private val iconWidth = icon.intrinsicWidth
    private val iconHeight = icon.intrinsicHeight
    private val iconMarginSide = context.resources.getDimension(R.dimen.item_padding).toInt()
    private val background: ColorDrawable = ColorDrawable()
    private val backgroundColor = ContextCompat.getColor(context, R.color.colorPrimaryLight)
    
    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false
    
    override fun onSwiped(
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        direction: Int
    ) {
        onSwiped(viewHolder.adapterPosition)
    }
    
    override fun onChildDraw(
        c: Canvas, recyclerView: androidx.recyclerview.widget.RecyclerView,
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.run { bottom - top }
        val absX = abs(dX).toInt()
        // the value -dX meant user swipes toward left: draw icon right-side
        // the value +dX meant user swipes toward right: draw icon left-side
        // "bound" meant background of itemView that shown by user swiping
        val boundTop = itemView.top
        val boundBottom = itemView.bottom
        val boundLeft = if (dX < 0) itemView.right - absX else 0
        val boundRight = if (dX < 0) itemView.right else absX
        
        c.clipRect(boundLeft, boundTop, boundRight, boundBottom)
        
        background.color = backgroundColor
        background.setBounds(boundLeft, boundTop, boundRight, boundBottom)
        background.draw(c)
        val iconMarginTop = (itemHeight - iconHeight) / 2
        val iconLeft = if (dX < 0) {
            itemView.right - iconWidth - iconMarginSide
        } else {
            boundLeft + iconMarginSide
        }
        val iconRight = iconLeft + iconWidth
        val iconTop = boundTop + iconMarginTop
        val iconBottom = iconTop + iconHeight
        
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)
        
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}