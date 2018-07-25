package jp.gcreate.product.filteredhatebu.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.view.View
import jp.gcreate.product.filteredhatebu.R
import timber.log.Timber

class StickyHeaderDecoration(context: Context, private val groupCallback: GroupCallback)
    : RecyclerView.ItemDecoration() {
    
    private val textPaint = TextPaint()
    private val backgroundPaint = Paint()
    private val headerPadding: Int
    private val headerMargin: Int
    private val headerHeight: Int
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
    
        headerPadding = resource.getDimensionPixelSize(R.dimen.sticky_header_padding)
        headerMargin = resource.getDimensionPixelSize(R.dimen.sticky_header_margin)
    
        headerHeight = (fontMetrics.run { leading - ascent } + (headerPadding + headerMargin) * 2).toInt()
    }
    
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (groupCallback.isBoundary(position)) {
            outRect.top = headerHeight
        }
    }
    
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val totalItemCount = state.itemCount
        val childCount = parent.childCount
        // yLimitはRecyclerViewの一番上の位置にHeaderを固定するときの位置
        val yLimit = headerHeight - headerPadding - headerMargin
        val lineHeight = fontMetrics.run { leading - ascent }
        var previousHeader: String = ""
        var groupId: Long = -1
        val layoutManager = parent.layoutManager as LinearLayoutManager
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        Timber.v("onDraw firstVisible position: $firstPosition/$lastPosition")
        val textX = (parent.width / 2).toFloat()
        
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
    
            if (groupCallback.isBoundary(position)) {
                view.top - headerMargin - headerPadding
            }
            val headerText = groupCallback.getGroupHeaderText(position)
            if (previousHeader != headerText) {
                // draw header text
                val viewTop = view.top + headerMargin + headerPadding
                val textY = if (groupCallback.isBoundary(position)) {
                    Math.max(yLimit, viewTop - headerPadding - headerMargin).toFloat()
                } else {
                    yLimit.toFloat()
                }
                c.drawText(headerText, textX, textY, textPaint)
                val textLength = textPaint.measureText(headerText) / 2
                val rectF = RectF(textX - textLength - headerPadding,
                                  textY - lineHeight - headerPadding,
                                  textX + textLength + headerPadding, textY + headerPadding)
                c.drawRoundRect(rectF, 15f, 15f, backgroundPaint)
        
                previousHeader = headerText
            }
        }
    }
    
    interface GroupCallback {
        fun getGroupId(position: Int): Long
        fun getGroupHeaderText(position: Int): String
        fun isBoundary(position: Int): Boolean
    }
}
