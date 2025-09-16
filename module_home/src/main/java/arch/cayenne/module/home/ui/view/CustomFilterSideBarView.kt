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

    // 文字顏色
    var textColorResId: Int = R.color.custom_filter_side_bar_text
        set(value) {
            field = value
            textColor = value.getColor()
            invalidate()
        }

    // 文字大小 (sp)
    var textSize: Float = 11f.sp2px
        set(value) {
            field = value
            requestLayout()
        }

    // 圓弧的寬度
    var ellipseWidth: Float = 50f.dp2px.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    // 圓弧的高度
    var ellipseHeight: Float = 70f.dp2px.toFloat()
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

    // 熱門圖標-選中
    var hotIconResId: Int = R.drawable.ic_hot_league_index
        set(value) {
            field = value
            hotIconBitmap = value.getDrawable().toBitmap()
            invalidate()
        }

    // 計算總數量
    val totalCount: Int
        get() = indexTitles.size

    // 比照 iOS 沒被選中的項目 透明度設為 0.3f
    private var unselectAlpha = 0.3f

    // 內部文字顏色變數
    private var textColor: Int

    // 內部 Bitmap 變數
    private var hotIconBitmap: Bitmap

    // 內部狀態
    private var itemHeight: Float = 0f
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
        hotIconBitmap = hotIconResId.getDrawable().toBitmap()
        textColor = textColorResId.getColor()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (indexTitles.isEmpty()) return

        textPaint.apply {
            textSize = this@CustomFilterSideBarView.textSize
            itemHeight = fontMetrics.bottom - fontMetrics.top
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (indexTitles.isEmpty()) return

        canvas.drawRect(
            width - normalViewWidth * 1.5f,
            0f,
            width - normalViewWidth * 0.5f,
            height.toFloat(),
            backgroundPaint.apply { color = Color.TRANSPARENT }
        )

        val maxX = width - normalViewWidth * 0.5f

        // 靜態狀態，沒有觸摸或動畫正在進行
        if (!isTouching) {
            for (i in 0 until totalCount) {
                val baseLineY = firstItemBaselineY + i * itemHeight
                val letter = indexTitles[i]
                val isSelected = i == selectedIndex
                val textColor = this@CustomFilterSideBarView.textColor

                val drawSize = normalViewWidth
                val rect = RectF(
                    maxX - drawSize / 2,
                    baseLineY - drawSize / 2,
                    maxX + drawSize / 2,
                    baseLineY + drawSize / 2
                )

                if (letter == "*") {
                    canvas.drawBitmap(
                        hotIconBitmap,
                        null, rect,
                        Paint().apply {
                            alpha = if (isSelected) 255 else (unselectAlpha * 255).toInt()
                        }
                    )
                } else {
                    canvas.drawText(
                        letter, maxX,
                        baseLineY - (textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2,
                        textPaint.apply {
                            textSize = this@CustomFilterSideBarView.textSize
                            color = textColor
                            alpha = if (isSelected) 255 else (unselectAlpha * 255).toInt()
                        }
                    )
                }
            }
            return
        }

        // 動態狀態，有觸摸在進行
        val currentTouchY = touchY

        val halfSizeY = ellipseHeight / 2f
        val topY = currentTouchY - halfSizeY
        val bottomY = currentTouchY + halfSizeY

        for (i in 0 until totalCount) {
            val baseLineY = firstItemBaselineY + i * itemHeight
            val letter = indexTitles[i]
            val isSelected = i == currentIndex

            val scale: Float
            val centerX: Float
            val alpha: Float

            val itemBottomY = baseLineY + itemHeight
            val isInRange = baseLineY <= bottomY && itemBottomY > topY

            if (isSelected) {
                // 如果是固定的選中項目，使用固定的位移和縮放
                scale = 1f
                centerX = maxX - ellipseWidth
                alpha = 1f
            } else if (!isInRange) {
                // 不在範圍內且未被選中的項目，保持靜態
                scale = 0f
                centerX = maxX
                alpha = unselectAlpha
            } else {
                // 在範圍內但未被選中的項目，進行動態計算
                val itemCenterY = baseLineY + itemHeight / 2

                // H5 程式碼中的 distanceY
                val distanceY = abs(itemCenterY - currentTouchY)

                // H5 程式碼中的 m (水平位移)
                val m = ellipseWidth * kotlin.math.sqrt(max(0f, 1f - (distanceY * distanceY) / (halfSizeY * halfSizeY)))

                // H5 程式碼中的 distancePercent
                val distancePercent = max(abs(distanceY / halfSizeY) - 0.1f, 0f)

                // 縮放比例
                val maxFontScale = 1f
                val minFontScale = 0f
                scale = max(maxFontScale - maxFontScale * distancePercent, minFontScale)

                // 位移
                centerX = maxX - m

                // 透明度
                // 原 H5 寫法為 max(distancePercent, 0.05f)
                // 但因比照 iOS，最低透明度為 unselectAlpha (0.3f)
                // 因此改寫為如下
                alpha = min(distancePercent, unselectAlpha)
            }

            // 繪製內容
            if (letter == "*") {
                val drawSize = normalViewWidth * (1 + scale)
                val rect = RectF(
                    centerX - drawSize / 2,
                    baseLineY - drawSize / 2,
                    centerX + drawSize / 2,
                    baseLineY + drawSize / 2
                )
                canvas.drawBitmap(
                    hotIconBitmap,
                    null, rect,
                    Paint().apply {
                        this.alpha = (alpha * 255).toInt()
                    }
                )
            } else {
                canvas.drawText(
                    letter, centerX,
                    baseLineY - (textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2,
                    textPaint.apply {
                        textSize = this@CustomFilterSideBarView.textSize * (1 + scale)
                        color = this@CustomFilterSideBarView.textColor
                        this.alpha = (alpha * 255).toInt()
                    }
                )
            }
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
        val letter = indexTitles.getOrNull(letterIndex) ?: ""

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

        if (currentIndex != -1) {
            selectedIndex = currentIndex
        } else {
            val y = event.y - firstItemBaselineY
            val index = (y / itemHeight).toInt()
            val letterIndex = min(max(0, index), totalCount - 1)
            selectedIndex = letterIndex
        }

        val letter = indexTitles.getOrNull(selectedIndex) ?: ""

        onIndexSelectedListener?.onIndexSelected(selectedIndex, letter, false)
        parent.requestDisallowInterceptTouchEvent(false)
        invalidate()
    }

    // 設置選中索引
    private fun setSelectedIndex(index: Int) {
        val minCheck = max(index, 0)
        selectedIndex = min(minCheck, totalCount - 1)
        invalidate()
    }

    /**
     * 設置選中標題
     * @param title 標題文字
     */
    fun setSelectedTitle(title: String) {
        val index = indexTitles.indexOf(title)
        if (index != -1) {
            setSelectedIndex(index)
        }
    }
}