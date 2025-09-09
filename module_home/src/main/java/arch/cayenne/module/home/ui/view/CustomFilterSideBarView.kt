package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.DimensionExt.sp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.module.home.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import arch.cayenne.lib.common.R as RC

class CustomFilterSideBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnIndexSelectedListener {
        fun onIndexSelected(index: Int, letter: String, isTouching: Boolean)
    }

    var onIndexSelectedListener: OnIndexSelectedListener? = null

    // Titles (預設為 A-Z)
    var indexTitles: List<String> = ('A'..'Z').map { it.toString() }
        set(value) {
            field = value
            requestLayout()
        }

    // 背景顏色
    var normalBackgroundColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    // 文字顏色
    var normalTextColorResId: Int = R.color.brand_color_index_unselect
        set(value) {
            field = value
            normalTextColor = value.getColor()
            invalidate()
        }

    // 選中文字顏色
    var selectedTextColorResId: Int = RC.color.brand_color
        set(value) {
            field = value
            selectedTextColor = value.getColor()
            invalidate()
        }

    // 文字大小 (sp)
    var textSize: Float = 11f.sp2px
        set(value) {
            field = value
            requestLayout()
        }

    // 最大偏移距離 (dp)
    var maxOffset: Float = 50f.dp2px.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    // 初始化一個預設的視圖寬度-外部也可以重新設定
    var normalViewWidth: Float = 16f.dp2px.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    // 熱門圖標
    var normalHotIconResId: Int = R.drawable.ic_hot_league_index_unselect
        set(value) {
            field = value
            normalHotIconBitmap = value.getDrawable().toBitmap()
            invalidate()
        }

    // 熱門圖標-選中
    var selectedHotIconResId: Int = R.drawable.ic_hot_league_index
        set(value) {
            field = value
            selectedHotIconBitmap = value.getDrawable().toBitmap()
            invalidate()
        }

    // 計算總數量
    val totalCount: Int
        get() = indexTitles.size

    // 內部文字顏色變數
    private var normalTextColor: Int
    private var selectedTextColor: Int

    // 內部 Bitmap 變數
    private var normalHotIconBitmap: Bitmap
    private var selectedHotIconBitmap: Bitmap

    // 內部狀態
    private var itemHeight: Float = 0f
    private var barHeight: Float = 0f
    private var firstItemBaselineY: Float = 16.dp2px.toFloat()
    private var currentIndex: Int = -1
    private var selectedIndex: Int = -1
    private var touchY: Float = -1f
    private var isTouching: Boolean = false

    // 文字 Paint
    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
        }

    // 背景 Paint
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        normalHotIconBitmap = normalHotIconResId.getDrawable().toBitmap()
        selectedHotIconBitmap = selectedHotIconResId.getDrawable().toBitmap()
        normalTextColor = normalTextColorResId.getColor()
        selectedTextColor = selectedTextColorResId.getColor()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (indexTitles.isEmpty()) return

        textPaint.apply {
            textSize = this@CustomFilterSideBarView.textSize
            itemHeight = fontMetrics.bottom - fontMetrics.top
            barHeight = indexTitles.size * itemHeight
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (indexTitles.isEmpty()) return

        // 重新繪製背景，不考慮 endPadding
        canvas.drawRect(
            width - normalViewWidth * 1.5f,
            0f,
            width - normalViewWidth * 0.5f,
            height.toFloat(),
            backgroundPaint.apply { color = normalBackgroundColor }
        )

        // 計算 maxX，現在它代表了靜態時的右側邊界
        val maxX = width - normalViewWidth * 0.5f

        for (i in 0 until totalCount) {
            val baseLineY = firstItemBaselineY + i * itemHeight
            val scale = calculateItemScale(i)
            val isSelected = i == currentIndex || i == selectedIndex
            val alpha = if (isSelected) 1.0f else (1 - scale + 0.05f)
            val textColor = if (isSelected) selectedTextColor else normalTextColor
            val letter = indexTitles[i]

            // 計算圓心 X，使其有向左凸出的圓弧效果
            val centerX = maxX - maxOffset * scale

            if(letter == "*") {
                run {
                    normalViewWidth * (1 + scale)
                }.let { drawSize ->
                    RectF(
                        centerX - drawSize / 2,
                        baseLineY - drawSize / 2,
                        centerX + drawSize / 2,
                        baseLineY + drawSize / 2
                    )
                }.let { rect ->
                    canvas.drawBitmap(
                        if (isSelected) selectedHotIconBitmap else normalHotIconBitmap,
                        null, rect,
                        Paint().apply {
                            this.alpha = (alpha * 255).toInt()
                        }
                    )
                }
            } else {
                canvas.drawText(
                    letter, centerX,
                    baseLineY - (textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2,
                    textPaint.apply {
                        textSize = this@CustomFilterSideBarView.textSize * (1 + scale)
                        color = textColor
                        this.alpha = (alpha * 255).toInt()
                    }
                )
            }
        }
    }

    // 計算每個項目的縮放比例
    private fun calculateItemScale(index: Int): Float {
        if (!isTouching || currentIndex == -1) return 0f

        return run {
            abs(touchY - (index * itemHeight + itemHeight / 2))
        }.run {
            max(0f, 1f - this / maxOffset)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchLeftBoundary = width - normalViewWidth * 2f
        val touchRightBoundary = width.toFloat()
        val touchX = event.x

        val isInTouchArea = touchX in touchLeftBoundary..touchRightBoundary

        if (!isInTouchArea) {
            if (isTouching) {
                handleTouchEnd(event)
            }
            return false
        }

        val y = event.y - firstItemBaselineY
        val index = (y / itemHeight).toInt()
        val letterIndex = min(max(0, index), totalCount - 1)
        val letter = indexTitles[letterIndex]

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                currentIndex = letterIndex
                touchY = y
                onIndexSelectedListener?.onIndexSelected(currentIndex, letter, true)
                parent.requestDisallowInterceptTouchEvent(true)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentIndex != letterIndex) {
                    currentIndex = letterIndex
                    onIndexSelectedListener?.onIndexSelected(currentIndex, letter, true)
                }
                touchY = y
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handleTouchEnd(event)
            }
        }
        return true
    }

    private fun handleTouchEnd(event: MotionEvent) {
        isTouching = false

        // 將選中的索引設定為最後滾動到的currentIndex
        // 如果currentIndex是有效的，就使用它
        if (currentIndex != -1) {
            selectedIndex = currentIndex
        } else {
            // 如果currentIndex無效，才 fallback 到最後的觸控點
            val y = event.y - firstItemBaselineY
            val index = (y / itemHeight).toInt()
            val letterIndex = min(max(0, index), totalCount - 1)
            selectedIndex = letterIndex
        }

        // 取得對應的 letter
        val letter = indexTitles[selectedIndex]

        // 清空 currentIndex
        currentIndex = -1

        onIndexSelectedListener?.onIndexSelected(selectedIndex, letter, false)
        parent.requestDisallowInterceptTouchEvent(false)
        invalidate()
    }


    /**
     * 設置選中索引
     */
    fun setSelectedIndex(index: Int) {
        val minCheck = max(index, 0)
        selectedIndex = min(minCheck, totalCount - 1)
        invalidate()
    }

    /**
     * 設置選中標題
     */
    fun setSelectedTitle(title: String) {
        val index = indexTitles.indexOf(title)
        if (index != -1) {
            setSelectedIndex(index)
        }
    }
}