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

/**
 * Note: This class supports LinearLayoutManager only.
 */
class StickyHeaderDecoration(context: Context, private val groupCallback: GroupCallback)
    : RecyclerView.ItemDecoration() {
    
    private val textPaint = TextPaint()
    private val backgroundPaint = Paint()
    private val headerPadding: Int
    private val headerMargin: Int
    // ヘッダーの高さ（背景の装飾を含めたヘッダーとして描画されるオブジェクトの高さ）
    private val headerHeight: Int
    // 文字のみの高さ
    private val textHeight: Int
    // ヘッダーの描画領域としてあける空間の高さ
    private val headerAreaOffset: Int
    
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
            alpha = 127
        }
        val fontMetrics = textPaint.fontMetrics
        
        headerPadding = resource.getDimensionPixelSize(R.dimen.sticky_header_padding)
        headerMargin = resource.getDimensionPixelSize(R.dimen.item_padding)
        
        textHeight = fontMetrics.run { leading - ascent }.toInt()
        headerHeight = textHeight + headerPadding * 2
        // 各アイテムの上部にpaddingがあり、offsetでは上部のmarginのみを考慮すれば良い
        headerAreaOffset = headerHeight + headerMargin
    }
    
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (groupCallback.isBoundary(position)) {
            outRect.top = headerAreaOffset
        }
    }
    
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        // yLimitはRecyclerViewの一番上の位置にHeaderを固定するときの位置
        val yLimit = headerMargin + textHeight
        var previousHeader = ""
        val layoutManager = parent.layoutManager as LinearLayoutManager
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        Timber.v("onDraw firstVisible position: $firstPosition/$lastPosition")
        val textX = (parent.width / 2).toFloat()
        
        if (firstPosition < 0 || lastPosition < 0) return
        for (i in firstPosition..lastPosition) {
            val view = parent.findViewHolderForAdapterPosition(i).itemView
            Timber.v("i($i) from $firstPosition/$lastPosition view=$view")
            val headerText = groupCallback.getGroupHeaderText(i)
            if (previousHeader != headerText) {
                // draw header text
                val viewTop = view.top - headerPadding
                val textY = if (groupCallback.isBoundary(i)) {
                    Math.max(yLimit, viewTop).toFloat()
                } else {
                    yLimit.toFloat()
                }
                val textLength = textPaint.measureText(headerText) / 2
                val rectF = RectF(textX - textLength - headerPadding,
                                  textY - textHeight - headerPadding,
                                  textX + textLength + headerPadding, textY + headerPadding)
                c.drawRoundRect(rectF, 15f, 15f, backgroundPaint)
                c.drawText(headerText, textX, textY, textPaint)
                
                previousHeader = headerText
            }
        }
    }
    
    interface GroupCallback {
        /**
         * 所属するグループのIDを返す
         */
        fun getGroupId(position: Int): Long
        
        /**
         * 所属するグループとして表示するヘッダーの文字列を返す
         */
        fun getGroupHeaderText(position: Int): String
        
        /**
         * ヘッダーを挿入する境界となるViewかどうかを返す
         * （List中の直前のアイテムとグループが異なればtrue, 直前のアイテムと同じならfalse）
         */
        fun isBoundary(position: Int): Boolean
    }
}
