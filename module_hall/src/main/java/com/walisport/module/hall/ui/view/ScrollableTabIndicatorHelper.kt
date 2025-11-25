package com.walisport.module.hall.ui.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.base.utils.LogUtils
import com.google.android.material.tabs.TabLayout

class ScrollableTabIndicatorHelper(
    private val tabLayout: TabLayout,
    private val bgView: View
) {
    private val loc = IntArray(2)
    private var pendingPos = -1

    private var isSyncNow = true  //是否执行跟手滑动

    private var mLeft = 5

    private var position = 1

     fun smartAnimateToCurrent() {
        val pos = tabLayout.selectedTabPosition
        if (pos < 0) return
         if (pos<2){
             tabLayout.postDelayed({ doAnimate()},if (isSyncNow)150 else 0)
         }else if(pos==2&&(isSyncNow||position<pos)){
             tabLayout.postDelayed({ doAnimate()},if (isSyncNow)150 else 0)
         }else if (pos>tabLayout.tabCount-2){
             tabLayout.postDelayed({ doAnimate()},if (isSyncNow)150 else 0)
         }else if(pos==tabLayout.tabCount-2&&(isSyncNow||position>pos)){
             tabLayout.postDelayed({ doAnimate()},if (isSyncNow)150 else 0)
         }
         if (pos==3){
             isSyncNow = true
         }
         if (pos==tabLayout.tabCount-3){
             isSyncNow = true
         }


         position = pos
    }

    private fun doAnimate() {
        val pos = tabLayout.selectedTabPosition
        if (pos < 0) return

        val tabView = (tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(pos) ?: return

        tabView.getLocationInWindow(loc)
        val tabCenter = loc[0] + tabView.width / 2f

        bgView.getLocationInWindow(loc)
        val bgCenter = loc[0] + bgView.width / 2f

        val targetX = bgView.translationX + tabCenter - bgCenter

        LogUtils.e("checkAndAnimateIfSettled-------->动画距离${targetX}")
        bgView.animate().apply {
            translationX((targetX-mLeft))
            setDuration(280)
            setInterpolator(FastOutSlowInInterpolator())
            withEndAction {
                isSyncNow  = false
            }
            start()
        }
    }

    // 手势滑动实时跟随
    private fun syncNow() {
        val pos = tabLayout.selectedTabPosition
        LogUtils.e("checkAndAnimateIfSettled-------->syncNow------->${pos}")
        if (pos < 0) return
        val tabView = (tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(pos) ?: return
        tabView.getLocationInWindow(loc)
        val tabCenter = loc[0] + tabView.width / 2f
        bgView.getLocationInWindow(loc)
        val bgCenter = loc[0] + bgView.width / 2f
        var targetX = bgView.translationX+(tabCenter - bgCenter-mLeft)
        bgView.translationX =targetX
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setup() {
        tabLayout.post { syncNow() }
        tabLayout.viewTreeObserver.addOnScrollChangedListener {
            syncNow()
        }
        // 关键：通过拦截 TabLayout 的触摸事件来判断“是否正在手势滑动”
        tabLayout.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    isSyncNow = true
                }
            }
            false  // 不拦截事件，让 TabLayout 正常处理点击和选择
        }
    }

    fun release() {
        pendingPos = -1
    }
}