package com.cn.game.sdk.ui.fast.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.databinding.FragmentHomeDefaultBinding
import com.xcjh.base_lib.utils.view.clickNoRepeat

class HomeDefaultFragment : BaseGameFragment<HomeDefaultVm, FragmentHomeDefaultBinding>() {
    var type:Int=0


    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt("type")
        }
        mDatabind.rlClick.setOnClickListener {
            Log.i("SSSSSSss","======"+it.x)
        }


        appGameViewModel.ceshEvent.observe(this){

        }
        //点击右上角
        mDatabind.rlClick.clickNoRepeat {
                Log.i("BBBBBb","==="+it.x)

        }
        clickRightTop()
    }

    /**
     * 点击右上角
     */
    @SuppressLint("ClickableViewAccessibility")
    fun  clickRightTop(){
        mDatabind.rlClick.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 获取点击位置的坐标
                    val x = event.x
                    val y = event.y
                    // 获取 View 的边界
                    val left = v.left.toFloat()
                    val top = v.top.toFloat()
                    val right = v.right.toFloat()
                    val bottom = v.bottom.toFloat()
                    // 定义边缘阈值，可根据实际情况调整
                    val edgeThreshold = 40f // 像素
                    // 判断点击位置是否在 View 的上下左右边缘
                    val isOnLeftEdge = x <= left + edgeThreshold
                    val isOnTopEdge = y <= top + edgeThreshold
                    val isOnRightEdge = x >= right - edgeThreshold
                    val isOnBottomEdge = y >= bottom - edgeThreshold
                    // 处理点击在边缘的逻辑
                    if (isOnLeftEdge || isOnTopEdge || isOnRightEdge || isOnBottomEdge) {
                        handleEdgeClick(isOnLeftEdge, isOnTopEdge, isOnRightEdge, isOnBottomEdge)
                    } else {
                        // 点击不在边缘
                        handleNonEdgeClick(x,y)
                    }
                }
            }
            true // 返回 true 表示事件已经被处理
        }
    }

    private fun handleEdgeClick(
        isOnLeftEdge: Boolean,
        isOnTopEdge: Boolean,
        isOnRightEdge: Boolean,
        isOnBottomEdge: Boolean
    ) {
        // 处理点击在边缘的逻辑
        if (isOnLeftEdge) {
            // 点击在左边缘
            Log.i("边缘","点击在左边缘")
        }
        if (isOnTopEdge) {
            // 点击在上边缘
            Log.i("边缘","点击在上边缘")
        }
        if (isOnRightEdge) {
            // 点击在右边缘
            Log.i("边缘","点击在右边缘")
        }
        if (isOnBottomEdge) {
            // 点击在下边缘
            Log.i("边缘","点击在下边缘")
        }
    }

    private fun handleNonEdgeClick(x:Float,y:Float) {
        // 处理点击不在边缘的逻辑
        Log.i("边缘","成功")
    }

    }
