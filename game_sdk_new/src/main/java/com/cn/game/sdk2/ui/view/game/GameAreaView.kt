package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.get
import com.cn.game.sdk2.data.bean.LocationClickPoint
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.websocket.bean.Betting
import kotlin.math.abs

class GameAreaView : FrameLayout {
    lateinit var content: View
    lateinit var gameCallback: IGameView
    lateinit var moneyView: MoneyOKView
    lateinit var flickerView:View
    var okViewGravity: Int = Gravity.BOTTOM
    var areaCode: Int = 0
    var areaInfo: Betting?= null
        set(value) {
            value?.number ?.let {
                areaCode = it
            }
            field = value
        }

    private var oldX = 0f
    private var oldY = 0f
    private var onLocationClickListener: LocationClickListener? = null
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        content = getChildAt(0)
        moneyView = MoneyOKView(context)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                oldX = event.x
                oldY = event.y
            }

            MotionEvent.ACTION_UP -> {
                val newX = event.x
                val newY = event.y
                if (abs(oldX - newX) < 5 && abs(oldY - newY) < 5) {
                    //处理点击事件
                    onLocationClickListener?.onLocationClick(newX,newY,event.rawX,event.rawY)
                }
            }
        }
        return true
    }

    /**
     * 触摸点击事件，返回点击的坐标信息
     */
    fun setOnLocationClickListener(listener:LocationClickListener){
        onLocationClickListener = listener
    }

    interface LocationClickListener {
        fun onLocationClick(x: Float, y: Float,rawX:Float,rawY:Float)
    }

}