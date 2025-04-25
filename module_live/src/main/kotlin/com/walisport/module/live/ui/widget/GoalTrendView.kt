package com.walisport.module.live.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportStringExt.timeStringToInt
import com.walisport.module.live.R
import com.walisport.module.live.data.EventEnum
import com.walisport.module.live.data.model.Incidents
import com.walisport.module.live.data.model.MatchTrendData
import galaxy.client.proto.Sloth

/**
 * 赛况页进球趋势View
 */

class GoalTrendView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val green = Paint()
    private val red = Paint()
    private val fiveRed = Paint()
    private val tenRed = Paint()
    private val blue = Paint()
    private val fiveBlue = Paint()
    private val tenBlue = Paint()

    private val tenRedColor: Int = 0x1AFE3666      //10%透明红色
    private val fiveRedColor: Int = 0x0DFE3666     //5%透明红色
    private val tenBlueColor: Int = 0x1A4150F6     //10%透明蓝色
    private val fiveBlueColor: Int = 0x0D4150F6    //5%透明蓝色

    private val football: Drawable? =
        ContextCompat.getDrawable(context, R.mipmap.icon_live_football)
    private val cornerBall: Drawable? = ContextCompat.getDrawable(context, R.mipmap.icon_live_jiao)
    private val yellowCard: Drawable? =
        ContextCompat.getDrawable(context, R.mipmap.icon_live_yellow)
    private val redCard: Drawable? = ContextCompat.getDrawable(context, R.mipmap.icon_live_red)
    private val changeCard: Drawable? = ContextCompat.getDrawable(context, R.mipmap.icon_live_out)

    private var eventList = ArrayList<Incidents>() //进攻事件列表
    private var trendList = ArrayList<Int>() //进攻趋势列表


    private val iconY = 93.dp2px //蓝队球赛事件图标y轴位置
    private val rectY = 14.dp2px.toFloat() //矩形背景y轴位置
    private val rectH = 19.dp2px.toFloat() //红矩形背景高度
    private val bgHigh = 38.dp2px.toFloat()//整个背景高度
    private var unitWidth = 3.dp2px.toFloat()//每单元的最大宽度
    private var lineWidth = 2.dp2px.toFloat()//每根竖线的宽度
    private var viewWidth = 0f //View控件的宽度

    init {
        green.color = 0xFF24EE8A.toInt()  //绿色
        green.style = Paint.Style.STROKE
        green.strokeWidth = lineWidth
        red.color = 0xFFFE3666.toInt()   //红色
        red.style = Paint.Style.STROKE
        red.strokeWidth = lineWidth
        blue.color = 0xFF4150F6.toInt()  //蓝色
        blue.style = Paint.Style.STROKE
        blue.strokeWidth = lineWidth
        fiveRed.color = fiveRedColor
        fiveRed.style = Paint.Style.FILL
        tenRed.color = tenRedColor
        tenRed.style = Paint.Style.FILL
        fiveBlue.color = fiveBlueColor
        fiveBlue.style = Paint.Style.FILL
        tenBlue.color = tenBlueColor
        tenBlue.style = Paint.Style.FILL
    }

    fun setData(data: MatchTrendData) {
        //只显示5种事件：1进攻 2角球 3黄牌 4红牌 9换人
        val array = data.incidents.filter {
            it.type == EventEnum.EVENT_GOAL.type ||
                    it.type == EventEnum.EVENT_CORNER.type ||
                    it.type == EventEnum.EVENT_YELLOW_CARD.type ||
                    it.type == EventEnum.EVENT_YELLOW_CARD.type ||
                    it.type == EventEnum.EVENT_CHANGE.type
        }
        eventList.clear()
        eventList.addAll(array)
        trendList.clear()
        trendList.addAll(data.data)
        this.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = measuredWidth.toFloat()
        unitWidth = viewWidth / 90f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制上面红色矩形背景
        canvas.drawRect(RectF(0f, rectY, viewWidth, rectH + rectY), fiveRed)
        //绘制中间红色矩形背景
        canvas.drawRect(RectF(0f, rectH + rectY, viewWidth, 2 * rectH + rectY), tenRed)
        //绘制中间蓝色矩形背景
        canvas.drawRect(RectF(0f, 2 * rectH + rectY, viewWidth, 3 * rectH + rectY), tenBlue)
        //绘制底部蓝色矩形背景
        canvas.drawRect(RectF(0f, 3 * rectH + rectY, viewWidth, 4 * rectH + rectY), fiveBlue)
        //绘制比赛趋势蜡柱图
        val trendSize = trendList.size
        if (trendSize > 0) {
            for (i in 0..<trendSize) {
                val left = i * unitWidth
                val right = left + lineWidth - 3
                val value = trendList[i]
                if (value > 0) {
                    val high = bgHigh * (value / 100f)
                    val top = rectY + bgHigh - high + 3
                    val bottom = top + high - 3
                    canvas.drawRect(RectF(left, top, right, bottom), red)
                } else {
                    val top = rectY + bgHigh + 6
                    val bottom = rectY + bgHigh - value + 6
                    canvas.drawRect(RectF(left, top, right, bottom), blue)
                }
            }
        }
        //绘制比赛事件图标
        val eventSize = eventList.size
        if (eventSize > 0) {
            for (i in 0..<eventSize) {
                val time = eventList[i].time.timeStringToInt()
                val type = eventList[i].type
                val pos = eventList[i].position
                //只显示5种事件：1进攻 2角球 3黄牌 4红牌 9换人
                val left = (time * unitWidth).toInt()
                when (type) {
                    EventEnum.EVENT_GOAL.type -> {
                        if (pos == 1) {//1-主队、2-客队
                            football?.setBounds(left, 0, left + 12.dp2px, 12.dp2px)
                        } else {
                            football?.setBounds(left, iconY, left + 12.dp2px, iconY + 12.dp2px)
                        }
                        football?.draw(canvas)
                    }

                    EventEnum.EVENT_CORNER.type -> {
                        if (pos == 1) {//1-主队、2-客队
                            cornerBall?.setBounds(left, 0, left + 12.dp2px, 12.dp2px)
                        } else {
                            cornerBall?.setBounds(left, iconY, left + 12.dp2px, iconY + 12.dp2px)
                        }
                        cornerBall?.draw(canvas)
                    }

                    EventEnum.EVENT_YELLOW_CARD.type -> {
                        if (pos == 1) {//1-主队、2-客队
                            yellowCard?.setBounds(left, 0, left + 12.dp2px, 12.dp2px)
                        } else {
                            yellowCard?.setBounds(left, iconY, left + 12.dp2px, iconY + 12.dp2px)
                        }
                        yellowCard?.draw(canvas)
                    }

                    EventEnum.EVENT_RED_CARD.type -> {
                        if (pos == 1) {//1-主队、2-客队
                            redCard?.setBounds(left, 0, left + 12.dp2px, 12.dp2px)
                        } else {
                            redCard?.setBounds(left, iconY, left + 12.dp2px, iconY + 12.dp2px)
                        }
                        redCard?.draw(canvas)
                    }

                    EventEnum.EVENT_CHANGE.type -> {
                        if (pos == 1) {//1-主队、2-客队
                            changeCard?.setBounds(left, 0, left + 12.dp2px, 12.dp2px)
                        } else {
                            changeCard?.setBounds(left, iconY, left + 12.dp2px, iconY + 12.dp2px)
                        }
                        changeCard?.draw(canvas)
                    }
                }
            }
        }
    }
}