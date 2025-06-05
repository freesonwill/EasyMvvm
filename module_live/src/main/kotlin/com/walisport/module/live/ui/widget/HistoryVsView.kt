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

/**
 *  @date: 2025/6/3 10:58
 *  @description: 展示两队交锋历史记录
 */
class HistoryVsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var historyVsList: List<Int>? = null

    private val itemWidth = 8.dp2px
    private val itemSpacing = 6.dp2px
    private val itemHeight = 8.dp2px

    private var paint: Paint = Paint()

    /**
     * 测量宽度
     */
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

                //红色表示输，绿色表示赢， 灰色表示打平
                val drawableId = when (ele) {
                    1 -> {
                        R.drawable.history_lose
                    }

                    -1 -> {
                        R.drawable.history_win
                    }

                    else -> {
                        R.drawable.history_tie
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
            //历史记录限定5个
            //take(n) 返回列表中前 n 个元素的子列表。
            //如果列表的元素少于 5 个，take(5) 会返回整个列表，不会抛出异常。
            //如果列表为空，返回空列表。
            historyVsList = historyVs.take(5)
            requestLayout()
        }
    }
}