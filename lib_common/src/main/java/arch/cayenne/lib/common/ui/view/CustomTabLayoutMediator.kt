package arch.cayenne.lib.common.ui.view

import android.animation.ValueAnimator
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper.Companion.getAnimHelper
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference
import kotlin.math.max

class CustomTabLayoutMediator(
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val autoRefresh: Boolean = true,
    private val tabConfigurationStrategy: TabConfigurationStrategy
) {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached = false
    private var skipAnyAnim = false
    private var isTabClick = false  // 儲存點擊狀態
    private var afterTabSelectedCallback: ((position: Int) -> Unit)? = null // 存储afterTabSelected回调

    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null

    fun scrollTabToCurrentPositionImmediately() {
        doOnClick(tabLayout.selectedTabPosition, true, true)
    }

    /**
     * 執行 TabLayout 滾動到指定位置
     * 使用自定義平滑滾動
     * @param position 目標位置
     * @param noTabAnim 是否不需要 TabLayout 的動畫效果
     * @param noViewPagerAnim 是否不需要 ViewPager2 的動畫效果
     */
    private fun doOnClick(
        position: Int,
        noTabAnim: Boolean = false,
        noViewPagerAnim: Boolean = false
    ) {
        try {
            if (!noTabAnim) {
                if (position != tabLayout.selectedTabPosition) {
                    smoothScrollToTab(position)
                }
            } else {
                tabLayout.scrollToPositionWithoutAnim(position)
                // 清空之前的切換紀錄
                viewPager.getAnimHelper().resetHistory()
            }
            skipAnyAnim = noViewPagerAnim
            tabLayout.getTabAt(position)?.select()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 滾動到指定 tab 位置
     */
    private fun smoothScrollToTab(position: Int) {
        // 原生 animateToTab
        if (ViewCompat.isLaidOut(tabLayout) &&
            doAnimateToTab(position)
        ) {
            return
        }

        //  自定義滾動
        if (customSmoothScroll(position)) {
            return
        }
    }

    /**
     * 原生 animateToTab 方法
     */
    private fun doAnimateToTab(position: Int): Boolean {
        return try {
            val method = TabLayout::class.java.getDeclaredMethod("animateToTab", Int::class.java)
            method.isAccessible = true
            method.invoke(tabLayout, position)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 自定義滾動（繞過 ViewCompat.isLaidOut 檢查）
     */
    private fun customSmoothScroll(position: Int): Boolean {
        return try {
            val slidingTabStrip = tabLayout.getChildAt(0) as? ViewGroup ?: return false
            val targetChild = slidingTabStrip.getChildAt(position) ?: return false
            val targetScrollX = (targetChild.left + targetChild.width / 2) - (tabLayout.width / 2)
            val maxScrollX = max(0, slidingTabStrip.width - tabLayout.width)
            val clampedScrollX = targetScrollX.coerceIn(0, maxScrollX)
            val current = tabLayout.scrollX
            ValueAnimator.ofInt(current, clampedScrollX).apply {
                duration = 300L
                interpolator = FastOutSlowInInterpolator()
                addUpdateListener { animator ->
                    tabLayout.scrollTo(animator.animatedValue as Int, 0)
                }
                start()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun interface TabConfigurationStrategy {
        fun onConfigureTab(tab: TabLayout.Tab, position: Int)
    }

    /**
     * 將 TabLayoutMediator 綁定到 TabLayout 和 ViewPager2。
     * @param afterTabSelected 可選的 CallBack 函式，在標籤切換動畫結束時觸發。
     */
    fun attach(afterTabSelected: ((position: Int) -> Unit)? = null) {
        if (attached) throw IllegalStateException("TabLayoutMediator is already attached")

        adapter = viewPager.adapter ?: throw IllegalStateException(
            "TabLayoutMediator attached before ViewPager2 has an adapter"
        )
        attached = true

        this.afterTabSelectedCallback = afterTabSelected

        viewPager.getAnimHelper().resetHistory()

        onPageChangeCallback = TabLayoutOnPageChangeCallback(tabLayout).also {
            viewPager.registerOnPageChangeCallback(it)
        }

        onTabSelectedListener = ViewPagerOnTabSelectedListener(viewPager, afterTabSelected).also {
            tabLayout.addOnTabSelectedListener(it)
        }

        if (autoRefresh) {
            pagerAdapterObserver = PagerAdapterObserver().also {
                adapter?.registerAdapterDataObserver(it)
            }
        }

        populateTabsFromPagerAdapter()
        tabLayout.setScrollPosition(viewPager.currentItem, 0f, true)

        if (tabLayout is CustomTabLayout) {
            // 設置自訂的 ClickListener
            tabLayout.onTabClick = { position ->
                isTabClick = true
                doOnClick(position, noTabAnim = false, noViewPagerAnim = false)
            }
        }
    }

    fun detach() {
        if (autoRefresh && adapter != null) {
            adapter?.unregisterAdapterDataObserver(pagerAdapterObserver!!)
            pagerAdapterObserver = null
        }

        tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback!!)
        onTabSelectedListener = null
        onPageChangeCallback = null
        adapter = null
        attached = false
    }

    fun selectTabWithoutAnimation(position: Int) {
        doOnClick(position = position, noTabAnim = true, noViewPagerAnim = true)
    }

    fun selectTabWithAnimation(position: Int) {
        doOnClick(position = position, noTabAnim = false, noViewPagerAnim = false)
    }

    private fun getAfterTabSelectedCallback(): ((position: Int) -> Unit)? {
        return afterTabSelectedCallback
    }

    internal fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()
        val itemCount = adapter?.itemCount ?: return

        for (i in 0 until itemCount) {
            val tab = tabLayout.newTab()
            tabConfigurationStrategy.onConfigureTab(tab, i)
            tabLayout.addTab(tab, false)
        }

        val currItem = viewPager.currentItem.coerceAtMost(tabLayout.tabCount - 1)
        if (currItem != tabLayout.selectedTabPosition) {
            tabLayout.selectTab(tabLayout.getTabAt(currItem))
        }
    }

    private inner class TabLayoutOnPageChangeCallback(tabLayout: TabLayout) :
        ViewPager2.OnPageChangeCallback() {
        private val tabLayoutRef = WeakReference(tabLayout)
        private var previousScrollState = ViewPager2.SCROLL_STATE_IDLE
        private var scrollState = ViewPager2.SCROLL_STATE_IDLE
        private var lastPosition = 0
        private var lastPositionOffset = 0f
        private var isUserScrolling = false

        private var targetPosition = 0 // 目标页面位置
        private var isUserInteracting = false // 用户是否正在交互
        private var currentPagePosition = 0 // 当前页面位置
        private var lastValidPosition = 0 // 最后一个有效位置，用于左滑时的位置校正
        private var isLeftSwiping = false // 标记是否正在左滑
        private var leftSwipeStartPosition = 0 // 左滑开始时的位置

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state

            // 更新滑动状态
            isUserScrolling = state == ViewPager2.SCROLL_STATE_DRAGGING
            isUserInteracting = state == ViewPager2.SCROLL_STATE_DRAGGING

            tabLayoutRef.get()?.let {
                try {
                    val method = TabLayout::class.java.getDeclaredMethod(
                        "updateViewPagerScrollState",
                        Int::class.java
                    )
                    method.isAccessible = true
                    method.invoke(it, scrollState)

                    if (it is CustomTabLayout) {
                        if (state == ViewPager2.SCROLL_STATE_IDLE) {
                            it.resetScrollState()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    // 开始拖动时记录当前页面位置
                    currentPagePosition = viewPager.currentItem
                    lastValidPosition = currentPagePosition
                    targetPosition = currentPagePosition
                    isLeftSwiping = false
                    leftSwipeStartPosition = currentPagePosition
                }

                ViewPager2.SCROLL_STATE_IDLE -> {
                    if (isUserInteracting && targetPosition != viewPager.currentItem) {
                        viewPager.setCurrentItem(targetPosition, true)
                        // TabLayoutMediator自動同步TabLayout的選中狀態
                    }
                    isUserInteracting = false
                    isLeftSwiping = false
                }
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabLayout = tabLayoutRef.get() ?: return

            if (position == lastPosition && positionOffset == lastPositionOffset) return

            lastPosition = position
            lastPositionOffset = positionOffset

            if (isUserInteracting) {
                val adapterItemCount = viewPager.adapter?.itemCount ?: 0
                if (adapterItemCount == 0) return

                when {
                    position == currentPagePosition -> {
                        if (positionOffset > 0.5f && position < adapterItemCount - 1) {
                            // 向右滑超過50%切換下一頁
                            targetPosition = position + 1
                            updateTabLayoutToPositionSmooth(
                                tabLayout,
                                targetPosition,
                                positionOffset
                            )
                        } else if (positionOffset < 0.5f) {
                            // 滑動未超過50%，恢復原頁
                            targetPosition = currentPagePosition
                            updateTabLayoutToPositionSmooth(
                                tabLayout,
                                targetPosition,
                                1f - positionOffset
                            )
                        }
                    }

                    position == currentPagePosition - 1 -> {
                        isLeftSwiping = true
                        leftSwipeStartPosition = currentPagePosition

                        if (positionOffset > 0.5f) {
                            // 左滑
                            targetPosition = position
                            lastValidPosition = position
                            updateTabLayoutToPositionSmooth(
                                tabLayout,
                                targetPosition,
                                positionOffset
                            )
                        } else {
                            // 恢復原頁面
                            if (positionOffset < 0.3f) {
                                targetPosition = currentPagePosition
                                updateTabLayoutToPositionSmooth(
                                    tabLayout,
                                    targetPosition,
                                    1f - positionOffset
                                )
                            } else {
                                targetPosition = position
                                updateTabLayoutToPositionSmooth(
                                    tabLayout,
                                    targetPosition,
                                    positionOffset
                                )
                            }
                        }
                    }

                    position == currentPagePosition + 1 -> {
                        // 右滑
                        if (positionOffset < 0.5f) {
                            // 恢復原頁
                            targetPosition = currentPagePosition
                            updateTabLayoutToPositionSmooth(
                                tabLayout,
                                targetPosition,
                                1f - positionOffset
                            )
                        } else {
                            // 滑超過50%，切換下一頁
                            targetPosition = position
                            lastValidPosition = position
                            updateTabLayoutToPositionSmooth(
                                tabLayout,
                                targetPosition,
                                positionOffset
                            )
                        }
                    }

                    else -> {
                        // 左滑
                        if (isLeftSwiping && position < leftSwipeStartPosition) {
                            if (position >= 0) {
                                targetPosition = position
                                updateTabLayoutToPositionSmooth(tabLayout, targetPosition, 0.5f)
                            }
                        } else if (position < currentPagePosition) {
                            targetPosition = maxOf(0, currentPagePosition - 1)
                            updateTabLayoutToPositionSmooth(tabLayout, targetPosition, 0.5f)
                        } else if (position > currentPagePosition) {
                            targetPosition = minOf(adapterItemCount - 1, currentPagePosition + 1)
                            updateTabLayoutToPositionSmooth(tabLayout, targetPosition, 0.5f)
                        }
                    }
                }
            }
        }

        private fun updateTabLayoutToPositionSmooth(
            tabLayout: TabLayout,
            position: Int,
            progress: Float
        ) {
            if (position < 0 || position >= tabLayout.tabCount) return

            try {
                val smoothPosition = when {
                    position == currentPagePosition -> currentPagePosition.toFloat()
                    position < currentPagePosition -> currentPagePosition - 1 + progress
                    else -> currentPagePosition + progress
                }

                // 使用反射直接更新TabLayout，不觸發ViewPager2事件
                val method = TabLayout::class.java.getDeclaredMethod(
                    "setScrollPosition",
                    Int::class.java,
                    Float::class.java,
                    Boolean::class.java,
                    Boolean::class.java
                )
                method.isAccessible = true
                method.invoke(tabLayout, position, smoothPosition - position, true, false)

                // 更新CustomTabLayout的預先選中狀態
                if (tabLayout is CustomTabLayout) {
                    tabLayout.setPreSelectedPosition(position)
                }

                val afterTabSelectedCallback = getAfterTabSelectedCallback()
                afterTabSelectedCallback?.invoke(position)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get() ?: return

            // 当ViewPager2明确地定位在新页面上时调用
            if (tabLayout is CustomTabLayout) {
                tabLayout.setPreSelectedPosition(position) // 确保CustomTabLayout视觉上提交到这个选择
            }

            if (position != tabLayout.selectedTabPosition && position < tabLayout.tabCount) {
                // 设置自定义的滑动效果
                doOnClick(position)
            }
        }
    }

    private inner class ViewPagerOnTabSelectedListener(
        private val viewPager: ViewPager2,
        private val afterTabSelected: ((position: Int) -> Unit)?
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            // 如果是 BounceTabLayoutContainer，則跳過回彈動畫
            (tab.parent?.parent as? BounceTabLayoutContainer)?.setSkipAnim(true)
            if (skipAnyAnim) {
                viewPager.setCurrentItem(tab.position, false)
                skipAnyAnim = false
            } else {
                // 判斷是否是點擊Tab觸發的，是的話執行淡入淡出動畫，否則執行滑動動畫
                if (isTabClick) {
                    viewPager.startFadeAnim { onComplete ->
                        viewPager.setCurrentItem(tab.position, false)
                        onComplete.invoke()
                    }
                    isTabClick = false
                } else {
                    viewPager.doSmartAnim(targetPosition = tab.position)
                }
            }
            afterTabSelected?.invoke(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    private inner class PagerAdapterObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = populateTabsFromPagerAdapter()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) =
            populateTabsFromPagerAdapter()

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) =
            populateTabsFromPagerAdapter()

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) =
            populateTabsFromPagerAdapter()

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
            populateTabsFromPagerAdapter()

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) =
            populateTabsFromPagerAdapter()
    }
}

fun TabLayout.scrollToPositionWithoutAnim(position: Int) {
    if (position == -1) return
    TabLayout::class.java
        .getDeclaredMethod(
            "setScrollPosition",
            Int::class.java,
            Float::class.java,
            Boolean::class.java,
            Boolean::class.java
        ).apply {
            isAccessible = true
            invoke(this@scrollToPositionWithoutAnim, position, 0f, true, false)
        }
}