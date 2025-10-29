package arch.cayenne.module.order.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class OrderItemSelectionDecoration(
    private val dividerHeight: Int = 1.dp2px,
    private val dividerColor: Int = arch.cayenne.lib.common.R.color.color_0FFFFFFF,
    private val dividerMarginStart: Int = 18.dp2px,
    private val dividerMarginEnd: Int = 18.dp2px
): RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }



    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // 設置分隔線顏色
        paint.color = ContextCompat.getColor(parent.context, dividerColor)
        
        val left = parent.paddingLeft + dividerMarginStart
        val right = parent.width - parent.paddingRight - dividerMarginEnd

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            val itemCount = parent.adapter?.itemCount ?: 0
            
            // 除了最後一個項目，其他都繪製分隔線
            if (position < itemCount - 1) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + dividerHeight

                canvas.drawRect(
                    left.toFloat(), 
                    top.toFloat(), 
                    right.toFloat(), 
                    bottom.toFloat(), 
                    paint
                )
            }
        }
    }
}