package com.xcjh.app.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.xcjh.app.R

/**
 * 首页下拉刷新自定义动画
 */
class CustomHeader (context: Context ) : RelativeLayout(context),RefreshHeader  {

    private lateinit var refreshLoadingImg: LottieAnimationView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_custom_header, this)
        refreshLoadingImg = view.findViewById(R.id.refreshLoadingImg)


    }

    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState) {
        // 刷新状态变化时的处理逻辑
        // 根据刷新状态更新自定义头部的显示内容
        when (newState) {
            RefreshState.None -> {

                // 无状态
//                ivCustom.setImageResource(R.drawable.ic_custom_header_none)
            }
            RefreshState.PullDownToRefresh -> {
//                refreshLoadingImg.visibility= View.VISIBLE
                //开始刷新
                refreshLoadingImg.playAnimation()

            }
            RefreshState.ReleaseToRefresh -> {
                //释放立即刷新

            }
            RefreshState.Refreshing -> {
                //开始刷新
            }

            RefreshState.RefreshFinish -> {
                //结束
                refreshLoadingImg.cancelAnimation()
            }
            else ->{

            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.FixedBehind //指定为平移，不能null

    }

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg colors: Int) {

    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
            // 初始化时的处理逻辑

    }

    @SuppressLint("RestrictedApi")
    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int) {
        // 下拉过程中的处理逻辑

    }

    @SuppressLint("RestrictedApi")
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        // 释放手指时的处理逻辑

    }

    @SuppressLint("RestrictedApi")
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        //刷新完成
//        refreshLoadingImg.visibility= View.INVISIBLE
//        refreshLoadingImg.cancelAnimation()

//        refreshLoadingImg.pauseAnimation()
        //延迟500毫秒之后再弹回
        return 0
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {

    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }


}