package arch.cayenne.lib.common.utils.ext

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

fun TabLayout.removeAllTips() {
    post {
        for (i in 0 until tabCount) {
            getTabAt(i)?.view?.let { tabView ->
                TooltipCompat.setTooltipText(tabView, null)
                tabView.setOnLongClickListener { false }
                tabView.isLongClickable = false
            }
        }
    }
}

 fun ViewPager2.setupViewPagerScroll(tabLayout: SkinnableTabLayout,customIndicator: CustomTabIndicator,tabIndicatorWidth : Float = 0.45f,skipAnyAnim: ((Boolean) -> Unit)? = null) {
    tabLayout.post {
        // 计算单个 Tab 的宽度
        val tabWidth = tabLayout.width.toFloat() / tabLayout.tabCount
        customIndicator.setTabWidth(tabWidth,tabIndicatorWidth)
    }
    var lastSwitchedPage: Int = 0 // 记录上一次切换的页面，防止重复切换
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    // 开始滑动时，记录初始页面位置并重置偏移量
                    skipAnyAnim?.invoke(false)
                    lastSwitchedPage = currentItem
                }

                ViewPager2.SCROLL_STATE_IDLE -> {
                    skipAnyAnim?.invoke(true)
                    // 滑动结束，基于初始页面和偏移量决定是否切换
                }
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val totalItems = adapter?.itemCount ?: 0
            val currentPage = currentItem
            val adjustedOffset = if (position == currentPage) {
                // 左滑
                positionOffset
            } else if (position == currentPage - 1) {
                // 右滑
                -(1.0f - positionOffset)
            } else {
                0.0f // 默认情况
            }
            // 左滑：adjustedOffset > 0.5，切换到下一页
            if (adjustedOffset > 0.5f && currentPage < totalItems - 1 && lastSwitchedPage != currentPage + 1) {
                lastSwitchedPage = currentPage + 1
                customIndicator.animateIndicatorToPosition(lastSwitchedPage, 200)
                tabLayout.getTabAt(lastSwitchedPage)?.select()
                LogUtils.e("setupViewPagerScroll-------左滑")
            }
            // 右滑：adjustedOffset < -0.5，切换到上一页
            else if (adjustedOffset < -0.5f && currentPage > 0 && lastSwitchedPage != currentPage - 1) {
                lastSwitchedPage = currentPage - 1
                customIndicator.animateIndicatorToPosition(lastSwitchedPage, 200)
                tabLayout.getTabAt(lastSwitchedPage)?.select()
                LogUtils.e("setupViewPagerScroll-------右滑")
            }
            // 滑动未超过 50%，恢复到当前页面
            else if (abs(adjustedOffset) <= 0.5f && lastSwitchedPage != currentPage) {
                lastSwitchedPage = currentPage
                customIndicator.animateIndicatorToPosition(lastSwitchedPage, 200)
                tabLayout.getTabAt(lastSwitchedPage)?.select()
                LogUtils.e("setupViewPagerScroll-------滑动未超过")
            }
        }
    })
    // 启用手动滑动
    isUserInputEnabled = true
}


fun ViewPager2.setupViewPagerScroll(positionCall: ((Int) -> Unit)? = null) {

    var lastSwitchedPage: Int = 0 // 记录上一次切换的页面，防止重复切换
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    lastSwitchedPage = currentItem
                }
            }
        }
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val totalItems = adapter?.itemCount ?: 0
            val currentPage = currentItem
            val adjustedOffset = if (position == currentPage) {
                // 左滑
                positionOffset
            } else if (position == currentPage - 1) {
                // 右滑
                -(1.0f - positionOffset)
            } else {
                0.0f // 默认情况
            }
            // 左滑：adjustedOffset > 0.5，切换到下一页
            if (adjustedOffset > 0.5f && currentPage < totalItems - 1 && lastSwitchedPage != currentPage + 1) {
                lastSwitchedPage = currentPage + 1
                positionCall?.invoke(lastSwitchedPage)
            }
            // 右滑：adjustedOffset < -0.5，切换到上一页
            else if (adjustedOffset < -0.5f && currentPage > 0 && lastSwitchedPage != currentPage - 1) {
                lastSwitchedPage = currentPage - 1
                positionCall?.invoke(lastSwitchedPage)
            }
            // 滑动未超过 50%，恢复到当前页面
            else if (abs(adjustedOffset) <= 0.5f && lastSwitchedPage != currentPage) {
                lastSwitchedPage = currentPage
                positionCall?.invoke(lastSwitchedPage)
            }
        }
    })
    // 启用手动滑动
    isUserInputEnabled = true
}



/**
 * 设置clip
 *
 * @param b
 */
@JvmOverloads
fun ViewPager2.setClipChilds(b: Boolean) {
    val view = getChildAt(0)
    if (view is RecyclerView) {
        view.clipChildren = b
        view.clipChildren = b
    }
}
