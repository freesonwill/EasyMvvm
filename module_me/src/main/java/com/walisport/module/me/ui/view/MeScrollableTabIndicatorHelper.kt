package com.walisport.module.hall.ui.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.base.utils.LogUtils
import com.google.android.material.tabs.TabLayout

class MeScrollableTabIndicatorHelper(
    private val tabLayout: TabLayout,
    private val bgView: View
) {
    private val loc = IntArray(2)
    private var pendingPos = -1

    private var isSyncNow = true  //是否执行跟手滑动

    private var mLeft = 0
    private var isSetup = false
    private var position = 1
    private var tabWiths: MutableList<Int> =mutableListOf()
    @SuppressLint("SuspiciousIndentation")
    fun smartAnimateToCurrent() {
        val pos = tabLayout.selectedTabPosition
            tabLayout.postDelayed({ doAnimate() }, 100 )
        isSyncNow = false
         LogUtils.e("MeScrollableTabIndicatorHelper-------->smartAnimateToCurrent------->${pos}")


        position = pos
    }

    private fun doAnimate() {
        val pos = tabLayout.selectedTabPosition

        val tabView = (tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(pos) ?: return

        tabView.getLocationInWindow(loc)
        val tabCenter = (loc[0] + tabView.width / 2f)-(tabWiths[pos]/1.15f)

        bgView.getLocationInWindow(loc)
        val bgCenter = loc[0] + bgView.width / 2f

        val targetX = bgView.translationX + tabCenter - bgCenter
         LogUtils.e("MeScrollableTabIndicatorHelper-------->动画距离${targetX},,,,,,,,,${tabWiths[pos]}------tabCenter:${tabCenter}")
        bgView.animate().apply {
            translationX((targetX - mLeft))
            setDuration(280)
            setInterpolator(FastOutSlowInInterpolator())
            withEndAction {
                isSyncNow = true
                if (bgView.isGone) {
                    bgView.visibility = View.VISIBLE
                }
            }
            start()
        }
    }

    // 手势滑动实时跟随
    private fun syncNow() {
        if (isSyncNow){
            val pos = tabLayout.selectedTabPosition
            val tabView = (tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(pos) ?: return
            tabView.getLocationInWindow(loc)
            val tabCenter = (loc[0] + tabView.width / 2f )-(tabWiths[pos]/1.15f)
            LogUtils.e("MeScrollableTabIndicatorHelper-------->syncNow------->${pos},,,,,,${tabWiths[pos]}------tabCenter:${tabCenter}")
            bgView.getLocationInWindow(loc)
            val bgCenter = loc[0] + bgView.width / 2f
            var targetX = bgView.translationX + (tabCenter - bgCenter - mLeft)
            bgView.translationX = targetX
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setup( tabWiths: MutableList<Int>) {
        this.tabWiths = tabWiths
            tabLayout.viewTreeObserver.addOnScrollChangedListener {
                syncNow()
            }
            tabLayout.setOnTouchListener { v, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        isSyncNow = true
                    }
                    MotionEvent.ACTION_UP -> {
                        isSyncNow = false
                    }
                }
                false
            }
    }

    fun release() {
        pendingPos = -1
    }
}