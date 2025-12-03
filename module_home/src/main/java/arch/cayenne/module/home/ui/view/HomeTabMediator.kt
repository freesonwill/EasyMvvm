package arch.cayenne.module.home.ui.view

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.ui.view.BounceTabLayoutContainer
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper.Companion.getAnimHelper
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * 首頁用, 基於 CustomTabLayoutMediator調整
 *
 * 功能：
 * - Drag > 50%, tab 與 indicator 預選
 * - 支援自定義 Tab 配置
 * - 水平滑動viewpager支援indicator移動動畫
 * - 點擊tab支援自定義indicator動畫速度
 * - 根據滑動速度智能調整動畫時長
 * - 防抖機制避免動畫衝突
 */
class HomeTabMediator(
    private val tabLayout: CustomTabLayout,
    private val viewPager: ViewPager2,
    private val tabConfiguration: (tab: TabLayout.Tab, position: Int) -> Unit,
    private val onPreselectChanged: ((position: Int) -> Unit)? = null
) {
    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached = false
    private var skipAnyAnim = false
    private var isTabClick = false
    private var afterTabSelectedCallback: ((position: Int) -> Unit)? = null

    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var indicatorAnimator: ValueAnimator? = null
    private var lastOnPageSelectedUptimeMs = 0L
    private var isDragging = false
    private var isClickAnimating = false

    // 動畫速度控制
    private var lastScrollTime = 0L
    private var lastScrollPosition = 0f
    private var scrollVelocity = 0f

    // 速度閾值配置
    companion object {
        // 滑動速度閾值
        private const val FAST_SCROLL_THRESHOLD = 3f      // 快速滑動閾值
        private const val VERY_FAST_SCROLL_THRESHOLD = 5.5f // 快速反覆滑動閾值
        private const val FAST_ANIMATION_RATIO = 0.8f       // 快速滑動時動畫時長比例
        private const val MIN_ANIMATION_DURATION = 150L     // 最小動畫時長
        private const val VERY_FAST_ANIMATION_DURATION = 100L // 快速反覆滑動動畫時長

        // 專門針對點擊tab和滑動viewpager的動畫時長配置
        private const val TAB_CLICK_ANIMATION_DURATION = 400L  // 點擊tab時的indicator動畫時長
        private const val SWIPE_ANIMATION_DURATION = 450L      // 滑動viewpager時的indicator動畫時長
        private const val DRAG_ANIMATION_DURATION = 250L       // drag預選時的動畫時長
    }

    // Tab 與 ViewPager 索引一一對應
    private fun toTabIndex(pageIndex: Int): Int = pageIndex

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

        populateTabsFromPagerAdapter()
        tabLayout.setScrollPosition(toTabIndex(viewPager.currentItem), 0f, true)

        tabLayout.onTabClick = { position ->
            isTabClick = true
            doOnClick(position, noViewPagerAnim = false)
        }
    }


    fun detach() {
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback!!)
        onTabSelectedListener = null
        onPageChangeCallback = null
        adapter = null
        attached = false
    }

    private fun doOnClick(
        position: Int,
        noViewPagerAnim: Boolean = false
    ) {
        try {
            viewPager.getAnimHelper().resetHistory()
            skipAnyAnim = noViewPagerAnim
            tabLayout.getTabAt(position)?.select()
        } catch (_: Exception) {
        }
    }

    //从PagerAdapter填充Tab
    private fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()
        val pagerCount = adapter?.itemCount ?: return
        for (i in 0 until pagerCount) {
            val tab = tabLayout.newTab()
            tabConfiguration.invoke(tab, i)
            tabLayout.addTab(tab, false)
        }
        val currItem = toTabIndex(viewPager.currentItem).coerceAtMost(tabLayout.tabCount - 1)
        if (currItem != tabLayout.selectedTabPosition) {
            tabLayout.selectTab(tabLayout.getTabAt(currItem))
        }
    }

    private inner class TabLayoutOnPageChangeCallback(tabLayout: TabLayout) :
        ViewPager2.OnPageChangeCallback() {
        private val tabLayoutRef = WeakReference(tabLayout)
        private var previousScrollState = ViewPager2.SCROLL_STATE_IDLE
        private var scrollState = ViewPager2.SCROLL_STATE_IDLE

        private var targetPosition = 0
        private var currentPagePosition = 0

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state

            tabLayoutRef.get()?.let {
                try {
                    val method = TabLayout::class.java.getDeclaredMethod(
                        "updateViewPagerScrollState",
                        Int::class.java
                    )
                    method.isAccessible = true
                    method.invoke(it, scrollState)
                    if (it is CustomTabLayout && state == ViewPager2.SCROLL_STATE_IDLE) it.resetScrollState()
                } catch (_: Exception) {
                }
            }

            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    currentPagePosition = viewPager.currentItem
                    targetPosition = currentPagePosition
                    isDragging = true
                    // 同步選中tab到當前頁
                    try {
                        tabLayout.selectTab(tabLayout.getTabAt(toTabIndex(viewPager.currentItem)))
                    } catch (_: Exception) {
                    }
                    // 取消未完成的動畫，避免在動畫過程中強制停止
                    if (indicatorAnimator?.isRunning == true) {
                        indicatorAnimator?.cancel()
                    }
                    // 重置滑動速度計算
                    lastScrollTime = 0L
                    lastScrollPosition = 0f
                    scrollVelocity = 0f
                }

                ViewPager2.SCROLL_STATE_IDLE -> {
                    // 只在drag過程中做預選，放手後讓 ViewPager2 決定最終位置
                    targetPosition = toTabIndex(viewPager.currentItem)
                    isDragging = false
                    // 停止拖曳階段的任何指示器動畫，避免與最終動畫衝突
                    indicatorAnimator?.cancel()

                    // 確保選中效果與最終頁面位置同步
                    try {
                        tabLayout.selectTab(tabLayout.getTabAt(targetPosition))
                    } catch (_: Exception) {
                    }

                    updateIndicatorPositionDirectly(tabLayout, targetPosition)
                    isClickAnimating = false
                }

                ViewPager2.SCROLL_STATE_SETTLING -> {
                    isDragging = false
                    // 進入慣性階段：停止拖曳預選動畫，偏移同步已在 onPageScrolled 持續進行
                    indicatorAnimator?.cancel()
                }
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabLayout = tabLayoutRef.get() ?: return
            if (adapter?.itemCount == 0) return
            // 點擊動畫進行中時，不做任何滑動同步，避免與點擊動畫打架
            if (isClickAnimating) return

            // 只有 DRAGGING 才處理預選與速度感知動畫，避免與結束定位衝突
            if (scrollState != ViewPager2.SCROLL_STATE_DRAGGING) return

            // 計算滑動速度
            val currentTime = System.currentTimeMillis()
            val currentPosition = position + positionOffset
            if (lastScrollTime > 0) {
                val timeDelta = currentTime - lastScrollTime
                val positionDelta = currentPosition - lastScrollPosition
                if (timeDelta > 0) {
                    scrollVelocity = abs(positionDelta) / timeDelta * 1000f // 每秒移動的頁面數
                }
            }
            lastScrollTime = currentTime
            lastScrollPosition = currentPosition

            // 雙向偏移：>0 左滑、<0 右滑
            val current = viewPager.currentItem
            val adjustedOffset = when {
                position == current -> positionOffset
                position == current - 1 -> -(1f - positionOffset)
                else -> 0f
            }

            val total = viewPager.adapter?.itemCount ?: 0
            val newTarget = when {
                adjustedOffset > 0.5f && current < total - 1 -> current + 1
                adjustedOffset < -0.5f && current > 0 -> current - 1
                else -> current  // 回到原始頁面或在中間位置
            }

            // Drag狀態僅做「預選樣式」更新，不對指示器做額外動畫，避免與偏移同步互相搶奪
            val to = toTabIndex(newTarget)
            if (to != targetPosition) {
                targetPosition = to
                updateTabSelectionWithoutPageChange(tabLayout, to)
                onPreselectChanged?.invoke(newTarget)
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get() ?: return
            val actual = toTabIndex(position)
            if (actual < 0 || actual >= tabLayout.tabCount) return

            // 檢查是否與當前選中的tab一致，避免重複設置
            val currentSelectedPosition = tabLayout.selectedTabPosition
            if (currentSelectedPosition == actual) {
                return
            }
            
            // 防抖：極短時間內連續選擇直接定位，不做動畫
            val now = android.os.SystemClock.uptimeMillis()
            if (now - lastOnPageSelectedUptimeMs < 60L) {
                try {
                    // 取消任何進行中的動畫
                    indicatorAnimator?.cancel()
                    updateIndicatorPositionDirectly(tabLayout, actual)
                    tabLayout.selectTab(tabLayout.getTabAt(actual))
                } catch (_: Exception) {
                }
                lastOnPageSelectedUptimeMs = now
                return
            }
            // 計算距離上次選擇的時間，判斷是否為點擊tab
            val timeSinceLastSelection = now - lastOnPageSelectedUptimeMs
            lastOnPageSelectedUptimeMs = now

            // 判斷是否為點擊tab觸發的頁面切換
            val isTabClick = timeSinceLastSelection > 300L

            // 僅在 ViewPager 最終定位改變時更新 Tab 與指示器
            val currentPage = toTabIndex(viewPager.currentItem)
            if (currentPage != actual) {
                try {
                    tabLayout.selectTab(tabLayout.getTabAt(actual))
                } catch (_: Exception) {
                }
                if (isTabClick) {
                    isClickAnimating = true
                    animateIndicatorForTabClick(tabLayout, currentPage, actual)
                } else {
                    updateIndicatorPositionDirectly(tabLayout, actual)
                }
            }
        }
    }

    /**
     * 直接更新指示器位置，不使用動畫
     * @param tabLayout TabLayout實例
     * @param position 目標位置
     */
    private fun updateIndicatorPositionDirectly(tabLayout: TabLayout, position: Int) {
        try {
            val method = TabLayout::class.java.getDeclaredMethod(
                "setScrollPosition",
                Int::class.java,
                Float::class.java,
                Boolean::class.java,
                Boolean::class.java
            )
            method.isAccessible = true
            method.invoke(tabLayout, position, 0f, false, true)
        } catch (_: Exception) {
        }
    }

    /**
     * 更新tab選中狀態但不觸發頁面跳轉
     * @param tabLayout TabLayout實例
     * @param position 目標位置
     */
    private fun updateTabSelectionWithoutPageChange(tabLayout: TabLayout, position: Int) {
        try {
            // 獲取目標tab
            val targetTab = tabLayout.getTabAt(position)
            if (targetTab != null) {
                // 直接調用selectTab，但由於isDragging標誌，ViewPagerOnTabSelectedListener會忽略這個調用
                tabLayout.selectTab(targetTab)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * 點擊tab時的專用indicator動畫
     * @param tabLayout TabLayout實例
     * @param from 起始位置
     * @param to 目標位置
     */
    private fun animateIndicatorForTabClick(tabLayout: TabLayout, from: Int, to: Int) {
        // 溫和地取消動畫：避免在動畫過程中強制停止
        if (indicatorAnimator?.isRunning == true) {
            indicatorAnimator?.cancel()
        }
        val forward = to > from
        val start = if (forward) 0f else 1f
        val end = if (forward) 1f else 0f

        indicatorAnimator = ValueAnimator.ofFloat(start, end).apply {
            this.duration = TAB_CLICK_ANIMATION_DURATION
            this.interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                val progress = anim.animatedValue as Float
                val offset = if (forward) progress else (1f - progress)
                try {
                    val method = TabLayout::class.java.getDeclaredMethod(
                        "setScrollPosition",
                        Int::class.java,
                        Float::class.java,
                        Boolean::class.java,
                        Boolean::class.java
                    )
                    method.isAccessible = true
                    if (progress < 1f) {
                        // 不更新 TabLayout 內建選中文字樣式，避免把自訂粗體還原
                        method.invoke(tabLayout, from, offset, false, true)
                    } else {
                        method.invoke(tabLayout, to, offset, false, true)
                    }
                } catch (_: Exception) {
                }
            }
            // 添加動畫完成回調，確保平滑過渡
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // 確保指示器最終位置正確
                    updateIndicatorPositionDirectly(tabLayout, to)
                }
            })
        }
        indicatorAnimator?.start()
    }

    /**
     * 根據滑動速度播放指示器動畫
     * @param tabLayout TabLayout實例
     * @param from 起始位置
     * @param to 目標位置
     */
    private fun animateIndicator(tabLayout: TabLayout, from: Int, to: Int) {
        // 溫和地取消動畫：避免在動畫過程中強制停止
        if (indicatorAnimator?.isRunning == true) {
            indicatorAnimator?.cancel()
        }

        // 根據滑動速度選擇動畫時長
        val duration = when {
            scrollVelocity > VERY_FAST_SCROLL_THRESHOLD -> {
                // 極快滑動時使用極短動畫時間，保持流暢感
                VERY_FAST_ANIMATION_DURATION
            }
            scrollVelocity > FAST_SCROLL_THRESHOLD -> {
                // 快速滑動時使用較短動畫時間，避免卡頓
                (DRAG_ANIMATION_DURATION * FAST_ANIMATION_RATIO).toLong()
                    .coerceAtLeast(MIN_ANIMATION_DURATION)
            }
            else -> {
                // 正常drag使用專門的動畫時間
                DRAG_ANIMATION_DURATION
            }
        }
        val forward = to > from
        val start = if (forward) 0f else 1f
        val end = if (forward) 1f else 0f

        indicatorAnimator = ValueAnimator.ofFloat(start, end).apply {
            this.duration = duration
            this.interpolator = LinearInterpolator()
            addUpdateListener { va ->
                val offset = va.animatedValue as Float
                try {
                    val method = TabLayout::class.java.getDeclaredMethod(
                        "setScrollPosition",
                        Int::class.java,
                        Float::class.java,
                        Boolean::class.java,
                        Boolean::class.java
                    )
                    method.isAccessible = true
                    if (forward) {
                        // 不更新 TabLayout 內建選中文字樣式，避免把自訂粗體還原
                        method.invoke(tabLayout, from, offset, false, true)
                    } else {
                        method.invoke(tabLayout, to, offset, false, true)
                    }
                } catch (_: Exception) {
                }
            }
            // 添加動畫完成回調，確保平滑過渡
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // 確保指示器最終位置正確
                    try {
                        val method = TabLayout::class.java.getDeclaredMethod(
                            "setScrollPosition",
                            Int::class.java,
                            Float::class.java,
                            Boolean::class.java,
                            Boolean::class.java
                        )
                        method.isAccessible = true
                        method.invoke(tabLayout, to, 0f, false, true)
                    } catch (_: Exception) {
                    }
                }
            })
        }
        indicatorAnimator?.start()
    }

    private inner class ViewPagerOnTabSelectedListener(
        private val viewPager: ViewPager2,
        private val afterTabSelected: ((position: Int) -> Unit)?
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            // 如果在drag過程中，忽略tab選中事件，避免強制跳轉頁面
            if (isDragging) return

            val target = tab.position
            (tab.parent?.parent as? BounceTabLayoutContainer)?.setSkipAnim(true)

            if (skipAnyAnim) {
                viewPager.setCurrentItem(target, false)
                skipAnyAnim = false
            } else if (isTabClick) {
                // 點擊時直接切換到對應頁（避免跳錯頁）
                isClickAnimating = true
                viewPager.setCurrentItem(target, false)
                isTabClick = false
            } else {
                // 其他情境保留平滑動畫
                viewPager.doSmartAnim(targetPosition = target)
            }
            afterTabSelected?.invoke(target)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }
}