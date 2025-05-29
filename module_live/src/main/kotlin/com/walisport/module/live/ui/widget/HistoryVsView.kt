package com.walisport.module.live.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.R

class HistoryVsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var historyVsList: List<Int>? = null

    private val itemWidth = 15.dp2px
    private val itemSpacing = 6.dp2px
    private val itemHeight = 15.dp2px

    private var paint: Paint = Paint()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = 0
        historyVsList?.let {
            width += it.size * itemWidth
            if (it.isNotEmpty()) {
                width += (it.size - 1) * itemSpacing
            }
        }

        setMeasuredDimension(width, itemHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        historyVsList?.let {
            it.forEachIndexed { index, ele ->

                val drawableId = when (ele) {
                    1 -> {
                        R.drawable.r
                    }
                    -1 -> {
                        R.drawable.b
                    }
                    else -> {
                        R.drawable.t
                    }
                }

                val bitmap = getDrawable(
                    context,
                    drawableId
                )!!.toBitmap(itemWidth, itemHeight)

                canvas.drawBitmap(bitmap, index * (itemWidth + itemSpacing).toFloat(), 0f, paint)
            }
        }
    }

    fun setData(historyVs: List<Int>) {
        post {
            historyVsList = historyVs
            requestLayout()
        }
    }
}