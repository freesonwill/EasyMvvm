package arch.cayenne.lib.common.utils.ext

import android.view.MotionEvent
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import com.google.android.material.tabs.TabLayout
import java.lang.Math.toDegrees
import kotlin.math.abs
import kotlin.math.atan2

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

/**
 * tabIndicatorWidth : 线条比例
 */
fun ViewPager2.setupViewPagerScroll(
    tabLayout: SkinnableTabLayout,
    customIndicator: CustomTabIndicator,
    tabIndicatorWidth: Float = 0.45f
) {
    tabLayout.post {
        // 计算单个 Tab 的宽度
        val tabWidth = tabLayout.width.toFloat() / tabLayout.tabCount
        customIndicator.setTabWidth(tabWidth, tabIndicatorWidth)
    }
    var lastSwitchedPage: Int = 0 // 记录上一次切换的页面，防止重复切换
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
                customIndicator.animateIndicatorToPosition(lastSwitchedPage)
                tabLayout.getTabAt(lastSwitchedPage)?.select()
            }
            // 右滑：adjustedOffset < -0.5，切换到上一页
            else if (adjustedOffset < -0.5f && currentPage > 0 && lastSwitchedPage != currentPage - 1) {
                lastSwitchedPage = currentPage - 1
                customIndicator.animateIndicatorToPosition(lastSwitchedPage)
                tabLayout.getTabAt(lastSwitchedPage)?.select()
            }
            // 滑动未超过 50%，恢复到当前页面
            else if (abs(adjustedOffset) <= 0.5f && lastSwitchedPage != currentPage) {
                lastSwitchedPage = currentPage
                customIndicator.animateIndicatorToPosition(lastSwitchedPage)
                tabLayout.getTabAt(lastSwitchedPage)?.select()
            }
        }
    })
    // 启用手动滑动
    isUserInputEnabled = true
    setupHorizontalScrollDegree()
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
fun ViewPager2.setClipChilds(b: Boolean = true) {
    val view = getChildAt(0)
    if (view is RecyclerView) {
        view.clipChildren = b
        view.clipChildren = b
    }
}

/**
 * 配置横向滚动角度，滚动角度小于d为横向，否则为竖向
 * @param d
 */
fun ViewPager2.setupHorizontalScrollDegree(d: Int = 25) {
    (getChildAt(0) as RecyclerView).apply {
        val lis = getTag(R.id.tag_on_item_touch_listener) as OnItemTouchListener?
        if (lis != null) removeOnItemTouchListener(lis)
        addOnItemTouchListener(object : OnItemTouchListener {
            private var mLastTouchX: Float = 0f
            private var mLastTouchY: Float = 0f
            private var hasJudged = false
            private var mTouchSlop = 0
            private val TAG = "ViewPager2"

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mLastTouchX = e.x
                        mLastTouchY = e.y
                        hasJudged = false
                        //"angle--------ACTION_DOWN->$mLastTouchX,$mLastTouchY".logd(TAG)
                        mTouchSlop = 5 //ViewConfiguration.get(context!!).scaledTouchSlop
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (hasJudged) return false
                        val dx = e.x - mLastTouchX
                        val dy = e.y - mLastTouchY
                        if (abs(dx) > mTouchSlop || abs(dy) > mTouchSlop) {
                            hasJudged = true
                            val angle = toDegrees(atan2(abs(dy.toDouble()), abs(dx.toDouble())))
                            if (angle < d) {
                                requestDisallowInterceptTouchEvent(false) //拦截，横向滑动
                            } else {
                                requestDisallowInterceptTouchEvent(true) //不拦截，竖向滑动
                            }
                            //"angle--------ACTION_MOVE->$angle,mLastTouchX:$mLastTouchX,mLastTouchY:$mLastTouchY,mTouchSlop:$mTouchSlop".logd(TAG)
                        }
                    }

                    MotionEvent.ACTION_UP -> {}
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                //"onTouchEvent----${MotionEvent.actionToString(e.action)},${e.x},${e.y}".logd(TAG)
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                //"onRequestDisallowInterceptTouchEvent----$disallowIntercept".logd(TAG)
            }
        }.apply { setTag(R.id.tag_on_item_touch_listener, this) })
    }
}