package arch.cayenne.module.home.ui.view

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.ext.startFadeAnim
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
    private val tabLayout: TabLayout,
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
        tabLayout.setScrollPosition(viewPager.currentItem, 0f, true)

        if (tabLayout is CustomTabLayout) {
            tabLayout.onTabClick = { position ->
                isTabClick = true
                doOnClick(position, noViewPagerAnim = false)
            }
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
            TabLayout::class.java
                .getDeclaredMethod(
                    "setScrollPosition",
                    Int::class.java,
                    Float::class.java,
                    Boolean::class.java
                ).apply {
                    isAccessible = true
                    invoke(tabLayout, position, 0f, true)
                }
            viewPager.getAnimHelper().resetHistory()
            skipAnyAnim = noViewPagerAnim
            tabLayout.getTabAt(position)?.select()
        } catch (_: Exception) {
        }
    }
    private fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()
        val itemCount = adapter?.itemCount ?: return
        for (i in 0 until itemCount) {
            val tab = tabLayout.newTab()
            tabConfiguration.invoke(tab, i)
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
                        tabLayout.selectTab(tabLayout.getTabAt(viewPager.currentItem))
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
                    targetPosition = viewPager.currentItem
                    isDragging = false
                    // 停止拖曳階段的任何指示器動畫，避免與最終動畫衝突
                    indicatorAnimator?.cancel()

                    // 確保選中效果與最終頁面位置同步
                    try {
                        tabLayout.selectTab(tabLayout.getTabAt(targetPosition))
                    } catch (_: Exception) {
                    }

                    updateIndicatorPositionDirectly(tabLayout, targetPosition)
                }

                ViewPager2.SCROLL_STATE_SETTLING -> {
                    isDragging = false
                    // 進入自動慣性滾動階段，停止拖曳預選動畫，避免異常滑動
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
            // Drag時處理預選與指示器動畫，避免點擊/程式切換造成來回切換
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

            // Drag狀態預選時更新
            if (newTarget != targetPosition) {
                val from = targetPosition
                val to = newTarget
                targetPosition = newTarget

                // Drag時以自訂動畫預選，手指放開後交由 TabLayout 內建動畫
                // 所有滑動都使用動畫，根據速度調整動畫時長
                animateIndicator(tabLayout, from, to)

                // 在drag過程中更新tab選中狀態，但不觸發頁面跳轉
                updateTabSelectionWithoutPageChange(tabLayout, to)
                
                onPreselectChanged?.invoke(to)
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get() ?: return
            if (position < 0 || position >= tabLayout.tabCount) return

            // 檢查是否與當前選中的tab一致，避免重複設置
            val currentSelectedPosition = tabLayout.selectedTabPosition
            if (currentSelectedPosition == position) {
                return
            }
            
            // 防抖：極短時間內連續選擇直接定位，不做動畫
            val now = android.os.SystemClock.uptimeMillis()
            if (now - lastOnPageSelectedUptimeMs < 60L) {
                try {
                    // 取消任何進行中的動畫
                    indicatorAnimator?.cancel()
                    updateIndicatorPositionDirectly(tabLayout, position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
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

            // 先設置選中狀態
            try {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            } catch (_: Exception) {
            }

            // 如果是點擊tab，添加自定義indicator動畫
            if (isTabClick) {
                val currentPage = viewPager.currentItem
                if (currentPage != position) {
                    animateIndicatorForTabClick(tabLayout, currentPage, position)
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
            if (isDragging) {
                return
            }
            
            (tab.parent?.parent as? BounceTabLayoutContainer)?.setSkipAnim(true)
            if (skipAnyAnim) {
                viewPager.setCurrentItem(tab.position, false)
                skipAnyAnim = false
            } else {
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
}