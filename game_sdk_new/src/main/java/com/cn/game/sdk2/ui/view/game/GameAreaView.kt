package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import com.cn.game.sdk2.data.bean.LocationClickPoint
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.ui.view.BetteView
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import kotlin.math.abs

class GameAreaView : FrameLayout {
    lateinit var content: View
    lateinit var moneyViewPair: Pair<MoneyOKView, BetteView>
    lateinit var flickerView: View
    var areaInfo: Betting? = null
    val areaCode: Int get() = areaInfo?.number ?: 0
    val moneyView: MoneyOKView get() = moneyViewPair.first
    val betteView: BetteView get() = moneyViewPair.second

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
        moneyViewPair = Pair(MoneyOKView(context), BetteView(context))
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
                if (abs(oldX - newX) < 20 && abs(oldY - newY) < 20) {
                    //处理点击事件
                    onLocationClickListener?.onLocationClick(event.rawX, event.rawY)
                }
            }
        }
        return true
    }

    fun setShowMoney(money: Int, updateBetteIcon: Boolean = true) {
        moneyViewPair.first.setShowMoney(money)
        if (updateBetteIcon) {
            moneyViewPair.second.updateBetteIcon(money)
        }
    }

    fun updateBetteIcon(money: Int) {
        moneyViewPair.second.updateBetteIcon(money)
    }

    fun removeChildViewFromParent() {
        if (moneyView.isAdd()) {
            val parent = moneyView.parent as ViewGroup
            parent.removeView(moneyView)
        }
        if (betteView.isAdd()) {
            val parent = betteView.parent as ViewGroup
            parent.removeView(betteView)
        }
    }

    fun againAdd(it: Map.Entry<Betting, BettingRecordBean>) {
        if (!moneyView.isAdd()) {
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            moneyView.let { moneyView ->
                moneyView.parentView?.addView(moneyView, params)
                moneyView.translationX = it.value.viewXYTemporary[0]
                moneyView.translationY = it.value.viewXYTemporary[1]
                moneyView.bringToFront()
            }
        }
        if (!betteView.isAdd()) {
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            betteView.let { betteView ->
                betteView.parentView?.addView(betteView, params)
                betteView.translationX = it.value.viewXYTemporary[0]
                betteView.translationY = it.value.viewXYTemporary[1]
            }
        }
    }


    /**
     * 触摸点击事件，返回点击的坐标信息
     */
    fun setOnLocationClickListener(listener: LocationClickListener) {
        onLocationClickListener = listener
    }

    interface LocationClickListener {
        fun onLocationClick(rawX: Float, rawY: Float)
    }

}