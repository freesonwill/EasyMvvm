package com.walisport.module.live.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.R
import com.walisport.module.live.data.model.GoalTrendBean

/**
 * 赛况页进球趋势View
 */

class GoalTrendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
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
    private val cornerKick: Drawable? = ContextCompat.getDrawable(context, R.mipmap.icon_live_jiao)
    private val yellowCard: Drawable? =
        ContextCompat.getDrawable(context, R.mipmap.icon_live_yellow)
    private val changeCard: Drawable? = ContextCompat.getDrawable(context, R.mipmap.icon_live_out)

    private var arrayList = ArrayList<GoalTrendBean>() //进攻趋势数据

    private val iconY = 93.dp2px //蓝队球赛事件图标y轴位置
    private val rectY = 14.dp2px.toFloat() //矩形背景y轴位置
    private val rectH = 19.dp2px.toFloat() //红矩形背景高度
    private val bgHigh = 38.dp2px.toFloat()//整个背景高度
    private var unitWidth = 3.dp2px.toFloat()//每单元的最大宽度
    private val lineWidth = 2.dp2px.toFloat()//每根竖线的宽度
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

    fun setData(array: ArrayList<GoalTrendBean>) {
        arrayList.clear()
        arrayList.addAll(array)
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
        if (arrayList.size > 0) {
            var last = 0f
            for (item in arrayList) {
                val left = item.minutes * unitWidth
                last = left + unitWidth
                val high = bgHigh * (item.rate / 100f)
                if (item.isHome) {
                    //绘制红队进攻趋势
                    val top = rectY + bgHigh - high
                    canvas.drawRect(RectF(left, top, left + lineWidth, top + high - 2), red)
                } else {
                    //绘制蓝队进攻趋势
                    val bottom = rectY + bgHigh + high
                    canvas.drawRect(RectF(left, rectY + bgHigh + 2, left + lineWidth, bottom), blue)
                }
                //开始绘制球赛事件
                val lf = left.toInt() - 4.dp2px
                when(item.type){
                    1 -> {
                        if (item.isHome) {
                            football?.setBounds(lf, 0, lf + 12.dp2px, 12.dp2px)
                        } else {
                            football?.setBounds(lf, iconY, lf + 12.dp2px, iconY + 12.dp2px)
                        }
                        football?.draw(canvas)
                    }
                    2 -> {
                        if (item.isHome) {
                            cornerKick?.setBounds(lf, 0, lf + 12.dp2px, 12.dp2px)
                        } else {
                            cornerKick?.setBounds(lf, iconY, lf + 12.dp2px, iconY + 12.dp2px)
                        }
                        cornerKick?.draw(canvas)
                    }
                    3 -> {
                        if (item.isHome) {
                            yellowCard?.setBounds(lf, 0, lf + 12.dp2px, 12.dp2px)
                        } else {
                            yellowCard?.setBounds(lf, iconY, lf + 12.dp2px, iconY + 12.dp2px)
                        }
                        yellowCard?.draw(canvas)
                    }
                    4 -> {
                        if (item.isHome) {
                            changeCard?.setBounds(lf, 0, lf + 12.dp2px, 12.dp2px)
                        } else {
                            changeCard?.setBounds(lf, iconY, lf + 12.dp2px, iconY + 12.dp2px)
                        }
                        changeCard?.draw(canvas)
                    }
                }
            }
            //绘制最后一根绿线
            canvas.drawRect(RectF(last + 2.dp2px, rectY, last + 2.dp2px + lineWidth, rectY + 2 * bgHigh), green)
        }
    }
}