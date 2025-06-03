package com.walisport.module.live.utils

import android.widget.LinearLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.google.android.material.tabs.TabLayout

/**
 * @author: aquan
 * @date: 2025/5/30 11:55
 * @description:
 */
object TabMarginExt {
    //设置tab之间的外边距
     fun reflexMargin(tabLayout: TabLayout,leftMargin:Int,rightMargin:Int,margin: Int) {
        tabLayout.post {
            try {
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    when (i) {
                        0 -> {//第一个tab
                            params.leftMargin = leftMargin
                            params.rightMargin = margin
                        }

                        mTabStrip.childCount - 1 -> {//最后一个tab
                            params.leftMargin = margin
                            params.rightMargin = rightMargin
                        }

                        else -> {//中间tab
                            params.leftMargin = margin
                            params.rightMargin = margin
                        }
                    }
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}