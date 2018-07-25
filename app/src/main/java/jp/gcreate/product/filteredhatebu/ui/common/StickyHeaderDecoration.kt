package jp.gcreate.product.filteredhatebu.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.view.View
import jp.gcreate.product.filteredhatebu.R
import timber.log.Timber

class StickyHeaderDecoration(context: Context, private val callback: Callback)
    : RecyclerView.ItemDecoration() {
    
    private val textPaint = TextPaint()
    private val backgroundPaint = Paint()
    private val labelPadding: Int
    private val contentMargin: Int
    private val fontMetrics: Paint.FontMetrics
    
    init {
        val resource = context.resources
        
        textPaint.apply {
            isAntiAlias = true
            textSize = resource.getDimension(R.dimen.sticky_header_text_size)
            color = ContextCompat.getColor(context, R.color.colorAccent)
            textAlign = Paint.Align.CENTER
        }
        
        backgroundPaint.apply {
            color = ContextCompat.getColor(context, R.color.colorPrimaryLight)
            alpha = 100
        }
        
        fontMetrics = textPaint.fontMetrics
        
        labelPadding = resource.getDimensionPixelSize(R.dimen.sticky_header_padding)
        contentMargin = resource.getDimensionPixelSize(R.dimen.sticky_header_margin)
    }
    
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val groupId = 0
        Timber.v("position: $position, view: $view, state: $state, parent: $parent")
        
        if (groupId < 0) {
            return
        }
        // TODO: 日付がかわるところおよび最初のアイテムの上にヘッダー用のoffsetを用意
    }
    
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val totalItemCount = state.itemCount
        val childCount = parent.childCount
        val lineHeight = fontMetrics.run { leading - ascent }
        var previousGroupId: Long
        var groupId: Long = -1
        
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            
            previousGroupId = groupId
            groupId = callback.getGroupId(position)
            
            if (groupId < 0 || previousGroupId == groupId) continue
            val textLine = callback.getGroupHeaderText(position)
            textLine ?: continue
            val textX = (view.width / 2).toFloat()
            val viewTop = view.top + view.paddingBottom
            var textY = Math.max(view.height, viewTop) - lineHeight
            if (position + 1 < totalItemCount) {
                val nextGroupId = callback.getGroupId(position + 1)
                if (nextGroupId != groupId && viewTop < textY + lineHeight) {
                    textY = viewTop - lineHeight
                }
            }
            val textLength = textPaint.measureText(textLine) / 2
            val rectF = RectF(textX - textLength - labelPadding, textY - lineHeight - labelPadding,
                              textX + textLength + labelPadding, textY + labelPadding)
            c.drawRoundRect(rectF, 15f, 15f, backgroundPaint)
            c.drawText(textLine, textX, textY, textPaint)
        }
        // TODO: 日付が同じ間はヘッダーを常に表示していたい、AbemaTVの番組表みたいな感じのをやりたい
    }
    
    interface Callback {
        fun getGroupId(position: Int): Long
        fun getGroupHeaderText(position: Int): String?
    }
}
