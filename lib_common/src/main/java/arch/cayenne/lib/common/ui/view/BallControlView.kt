package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
import kotlin.properties.Delegates


/**
 * @author banli
 * @date 2025/4/28
 * @description :球队双方进攻意图柱状图
 */
class BallControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //默认一场比赛90分钟
    private val defaultMaxTime: Int = 90

    //一分钟
    private val timeUnitM = 1000L * 60


    //柱状
    private val redColumnarPaint: Paint = Paint()
    private val blueColumnarPaint: Paint = Paint()
    private var columnarStrokeWidth = 0f
    private var columnarStrokeMargin = 0f

    //背景

    private val backgroundColorList = mutableListOf<Int>()
    private val backGroundPaint: Paint = Paint()

    //进度指示器
    private val processPaint: Paint = Paint()
    private val processPaintWidth = 20f.px2dp.toFloat()

    //控件宽高
    private var mHeight by Delegates.notNull<Int>()
    private var mWith by Delegates.notNull<Int>()


    //事件集合
    private val listEvent = mutableListOf<BallControlEvent>()

    //比赛时长,单位分钟
    private var maxTimeM: Int = 0

    //一分钟占的宽度
    private var widthByM: Float = 0f

    //每个柱状单元代表的分钟数，经测试代表2分钟才能满足ui需求
    private var columnarM = 2


    init {
        setCommonPaintStyle(redColumnarPaint, Color.RED)
        setCommonPaintStyle(blueColumnarPaint, Color.BLUE)
        setCommonPaintStyle(processPaint, Color.GREEN)
        backGroundPaint.isAntiAlias = true
    }


    private fun setCommonPaintStyle(paint: Paint, @ColorInt color: Int) {
        paint.isAntiAlias = true
        paint.setColor(color)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mWith = MeasureSpec.getSize(widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawColumnar(canvas)
        drawProcess(canvas)
    }

    private fun drawProcess(canvas: Canvas) {
        if (listEvent.isEmpty()) return
        val lastItemTime = listEvent.last().timestamp / timeUnitM
        if (lastItemTime < maxTimeM) {//比赛没有结束
            val marginLeft = (lastItemTime * widthByM).toInt()
            canvas.drawRect(
                Rect(marginLeft, 0, (marginLeft + processPaintWidth).toInt(), mHeight),
                processPaint
            )
        }
    }

    private fun checkMaxTime(time: Long) {
        val timeM: Int = (time / timeUnitM).toInt()

        if (timeM > maxTimeM) {
            maxTimeM = timeM
        }
        if (maxTimeM < defaultMaxTime) {
            maxTimeM = defaultMaxTime
        }

        //一分钟占的宽度
        widthByM = mWith * 1f / maxTimeM
        columnarStrokeWidth = widthByM * columnarM * 2 / 3
        columnarStrokeMargin = widthByM * columnarM * 1 / 3
    }

    private fun drawColumnar(canvas: Canvas) {
        val centerX = mHeight / 2
        listEvent.forEach { item ->
            val timeM: Int = (item.timestamp / timeUnitM).toInt()
            val marginLeft = ((widthByM * (timeM - columnarM)) + columnarStrokeMargin).toInt()
            if (item.isRed) {
                canvas.drawRect(
                    Rect(
                        marginLeft,
                        (centerX * (1 - item.heightPercent)).toInt(),
                        (marginLeft + columnarStrokeWidth).toInt(),
                        centerX
                    ), redColumnarPaint
                )
            } else {
                canvas.drawRect(
                    Rect(
                        marginLeft,
                        centerX,
                        (marginLeft + columnarStrokeWidth).toInt(),
                        (centerX * (1 + item.heightPercent)).toInt()
                    ), blueColumnarPaint
                )
            }
        }
    }

    /**
     *绘制背景
     */
    private fun drawBackground(canvas: Canvas) {
        if (backgroundColorList.isEmpty()) return
        var currentTop = 0
        // 将高度均分为n份
        val sectionHeight = mHeight / backgroundColorList.size
        backgroundColorList.forEach {item->
            backGroundPaint.setColor(item)
            canvas.drawRect(
                Rect(0, currentTop, mWith, currentTop + sectionHeight),
                backGroundPaint
            )
            // 更新下一个区块的顶部位置
            currentTop += sectionHeight
        }
    }


    fun updateEventList(backgroundColorList: List<Int>, listEvent: List<BallControlEvent>, maxTime: Long) {
        this.post {
            this.backgroundColorList.clear()
            this.backgroundColorList.addAll(backgroundColorList)
            this.listEvent.clear()
            this.listEvent.addAll(listEvent)
            checkMaxTime(maxTime)
            postInvalidate()
        }
    }

    fun updateEvent(backgroundColorList: List<Int>,event: BallControlEvent) {
        this.post {
            this.listEvent.add(event)
            updateEventList(backgroundColorList,this.listEvent, event.timestamp)
        }
    }


    data class BallControlEvent(
        val timestamp: Long,
        val isRed: Boolean,
        val heightPercent: Float
    ) {
        init {
            require(heightPercent in 0.0f..1.0f) {
                "heightPercent must be between 0 and 1, inclusive. Current value: $heightPercent"
            }
        }
    }
}