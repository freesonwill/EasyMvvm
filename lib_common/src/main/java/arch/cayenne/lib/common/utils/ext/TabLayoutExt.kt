package arch.cayenne.lib.common.utils.ext

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * @date: 2025/5/30 11:55
 * @description: tabLayout扩展
 */
object TabLayoutExt {
    //设置tab之间的外边距
    fun TabLayout.reflexMargin(leftMargin: Int, rightMargin: Int, margin: Int) {
        val tabLayout = this
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
        }
    }

    /**
     * OnTabSelectedListener增加isTabClick属性
     */
    abstract class OnTabSelectedListener1(tab:TabLayout):OnTabSelectedListener {
        private var isTabClick = false
        abstract fun onTabSelected(tab: Tab, isTabClick: Boolean)
        open fun onTabUnselected(tab: Tab, isTabClick: Boolean) {}
        open fun onTabReselected(tab: Tab, isTabClick: Boolean) {}

        init {
            //setOnClickListener
            tab.findViewTreeLifecycleOwner()!!.lifecycleScope.launch {
                withTimeout(200) { while (tab.tabCount == 0) delay(1) }
                for (i in 0 until tab.tabCount) {
                    val tabView = (tab.getChildAt(0) as ViewGroup).getChildAt(i)
                    tabView.setOnClickListener {
                        isTabClick = true
                        tab.post { isTabClick = false }
                    }
                }
            }
        }

        final override fun onTabSelected(tab: Tab) {
            onTabSelected(tab,isTabClick)
        }

        final override fun onTabReselected(tab: Tab) {
            onTabReselected(tab,isTabClick)
        }

        final override fun onTabUnselected(tab: Tab) {
            onTabUnselected(tab,isTabClick)
        }

    }

    /**
     * OnTabSelectedListener增加isTabClick属性
     */
    interface OnTabSelectedListener2 {
        fun onTabSelected(tab: Tab, isTabClick: Boolean)
        fun onTabUnselected(tab: Tab, isTabClick: Boolean)
        fun onTabReselected(tab: Tab, isTabClick: Boolean)
    }

    fun TabLayout.addOnTabSelectedListener2(lis: OnTabSelectedListener2) {
        clearOnTabSelectedListener()
        var isTabClick = false
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                lis.onTabSelected(tab, isTabClick)
            }

            override fun onTabUnselected(tab: Tab) {
                lis.onTabUnselected(tab, isTabClick)
            }

            override fun onTabReselected(tab: Tab) {
                lis.onTabReselected(tab, isTabClick)
            }
        }.apply { setTag(88888888, this) })

        //setOnClickListener
        findViewTreeLifecycleOwner()!!.lifecycleScope.launch {
            withTimeout(200) { while (tabCount == 0) delay(1) }
            for (i in 0 until tabCount) {
                val tabView = (getChildAt(0) as ViewGroup).getChildAt(i)
                tabView.setOnClickListener {
                    isTabClick = true
                    post { isTabClick = false }
                }
            }
        }
    }

    fun TabLayout.clearOnTabSelectedListener() {
        removeOnTabSelectedListener(getTag(88888888) as OnTabSelectedListener?)
    }
}

