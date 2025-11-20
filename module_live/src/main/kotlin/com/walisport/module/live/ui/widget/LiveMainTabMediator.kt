package arch.cayenne.module.home.ui.view

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.BounceTabLayoutContainer
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper.Companion.getAnimHelper
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * 直播间，基于 LiveCustomTabLayoutMediator 调整
 *
 * 功能：
 * - Drag > 50%, tab 与 indicator 预选
 * - 支持自定义 Tab 配置
 * - 水平滑动 viewpager 支持 indicator 移动动画
 * - 点击 tab 支持自定义 indicator 动画速度
 * - 根据滑动速度智能调整动画时长
 * - 防抖机制避免动画冲突
 */
class LiveMainTabMediator(
    private val tabLayout: LiveCustomTabLayout,
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

    // 动画速度控制
    private var lastScrollTime = 0L
    private var lastScrollPosition = 0f
    private var scrollVelocity = 0f

    // 速度阈值配置
    companion object {
        // 滑动速度阈值
        private const val FAST_SCROLL_THRESHOLD = 3f      // 快速滑动阈值
        private const val VERY_FAST_SCROLL_THRESHOLD = 5.5f // 快速反复滑动阈值
        private const val FAST_ANIMATION_RATIO = 0.8f       // 快速滑动时动画时长比例
        private const val MIN_ANIMATION_DURATION = 150L     // 最小动画时长
        private const val VERY_FAST_ANIMATION_DURATION = 100L // 快速反复滑动动画时长

        // 专门针对点击 tab 和滑动 viewpager 的动画时长配置
        private const val TAB_CLICK_ANIMATION_DURATION = 400L  // 点击 tab 时的 indicator 动画时长
        private const val SWIPE_ANIMATION_DURATION = 450L      // 滑动 viewpager 时的 indicator 动画时长
        private const val DRAG_ANIMATION_DURATION = 250L       // drag 预选时的动画时长
    }

    // Tab 与 ViewPager 索引一一对应
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
                    if (it is LiveCustomTabLayout && state == ViewPager2.SCROLL_STATE_IDLE) it.resetScrollState()
                } catch (_: Exception) {
                }
            }

            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    currentPagePosition = viewPager.currentItem
                    targetPosition = currentPagePosition
                    isDragging = true
                    // 同步选中 tab 到当前页
                    try {
                        tabLayout.selectTab(tabLayout.getTabAt(toTabIndex(viewPager.currentItem)))
                    } catch (_: Exception) {
                    }
                    // 取消未完成的动画，避免在动画过程中强制停止
                    if (indicatorAnimator?.isRunning == true) {
                        indicatorAnimator?.cancel()
                    }
                    // 重置滑动速度计算
                    lastScrollTime = 0L
                    lastScrollPosition = 0f
                    scrollVelocity = 0f
                }

                ViewPager2.SCROLL_STATE_IDLE -> {
                    // 只在 drag 过程中做预选，放手后让 ViewPager2 决定最终位置
                    targetPosition = toTabIndex(viewPager.currentItem)
                    isDragging = false
                    // 停止拖曳阶段的任何指示器动画，避免与最终动画冲突
                    indicatorAnimator?.cancel()

                    // 确保选中效果与最终页面位置同步
                    try {
                        tabLayout.selectTab(tabLayout.getTabAt(targetPosition))
                    } catch (_: Exception) {
                    }

                    updateIndicatorPositionDirectly(tabLayout, targetPosition)
                    isClickAnimating = false
                }

                ViewPager2.SCROLL_STATE_SETTLING -> {
                    isDragging = false
                    // 进入惯性阶段：停止拖曳预选动画，偏移同步已在 onPageScrolled 持续进行
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
            // 点击动画进行中时，不做任何滑动同步，避免与点击动画打架
            if (isClickAnimating) return

            // 只有 DRAGGING 才处理预选与速度感知动画，避免与结束定位冲突
            if (scrollState != ViewPager2.SCROLL_STATE_DRAGGING) return

            // 计算滑动速度
            val currentTime = System.currentTimeMillis()
            val currentPosition = position + positionOffset
            if (lastScrollTime > 0) {
                val timeDelta = currentTime - lastScrollTime
                val positionDelta = currentPosition - lastScrollPosition
                if (timeDelta > 0) {
                    scrollVelocity = abs(positionDelta) / timeDelta * 1000f // 每秒移动的页面数
                }
            }
            lastScrollTime = currentTime
            lastScrollPosition = currentPosition

            // 双向偏移：>0 左滑、<0 右滑
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
                else -> current  // 回到原始页面或在中间位置
            }

            // Drag 状态仅做「预选样式」更新，不对指示器做额外动画，避免与偏移同步互相抢夺
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

            // 检查是否与当前选中的 tab 一致，避免重复设置
            val currentSelectedPosition = tabLayout.selectedTabPosition
            if (currentSelectedPosition == actual) {
                return
            }

            // 防抖：极短时间内连续选择直接定位，不做动画
            val now = android.os.SystemClock.uptimeMillis()
            if (now - lastOnPageSelectedUptimeMs < 60L) {
                try {
                    // 取消任何进行中的动画
                    indicatorAnimator?.cancel()
                    updateIndicatorPositionDirectly(tabLayout, actual)
                    tabLayout.selectTab(tabLayout.getTabAt(actual))
                } catch (_: Exception) {
                }
                lastOnPageSelectedUptimeMs = now
                return
            }
            // 计算距离上次选择的时间，判断是否为点击 tab
            val timeSinceLastSelection = now - lastOnPageSelectedUptimeMs
            lastOnPageSelectedUptimeMs = now

            // 判断是否为点击 tab 触发的页面切换
            val isTabClick = timeSinceLastSelection > 300L

            // 仅在 ViewPager 最终定位改变时更新 Tab 与指示器
            val currentPage = toTabIndex(viewPager.currentItem)
            if (currentPage != actual) {
                try {
                    tabLayout.selectTab(tabLayout.getTabAt(actual))
                } catch (_: Exception) {
                }
                LogUtils.e("LiveMainTabMediator------->animateIndicatorForTabClick--------->isTabClick${isTabClick}")
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
     * 直接更新指示器位置，不使用动画
     * @param tabLayout TabLayout 实例
     * @param position 目标位置
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
     * 直接更新指示器位置，不使用动画
     * @param tabLayout TabLayout 实例
     * @param position 目标位置
     */
    public fun selectPositionNoAnim(tabLayout: TabLayout, position: Int) {
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
            tabLayout.getTabAt(position)?.select()
        } catch (_: Exception) {
        }
    }

    /**
     * 更新 tab 选中状态但不触发页面跳转
     * @param tabLayout TabLayout 实例
     * @param position 目标位置
     */
    private fun updateTabSelectionWithoutPageChange(tabLayout: TabLayout, position: Int) {
        try {
            // 获取目标 tab
            val targetTab = tabLayout.getTabAt(position)
            if (targetTab != null) {
                // 直接调用 selectTab，但由于 isDragging 标志，ViewPagerOnTabSelectedListener 会忽略这个调用
                tabLayout.selectTab(targetTab)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * 点击 tab 时的专用 indicator 动画
     * @param tabLayout TabLayout 实例
     * @param from 起始位置
     * @param to 目标位置
     */
    private fun animateIndicatorForTabClick(tabLayout: TabLayout, from: Int, to: Int) {
        // 温和地取消动画：避免在动画过程中强制停止
        LogUtils.e("LiveMainTabMediator------->animateIndicatorForTabClick--------->isRunning${indicatorAnimator?.isRunning}")
        if (indicatorAnimator?.isRunning == true) {
            indicatorAnimator?.cancel()
        }
        val forward = to > from
        val start = if (forward) 0f else 1f
        val end = if (forward) 1f else 0f
        LogUtils.e("LiveMainTabMediator------->indicatorAnimator")
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
                        // 不更新 TabLayout 内置选中文字样式，避免把自定义粗体还原
                        method.invoke(tabLayout, from, offset, false, true)
                    } else {
                        method.invoke(tabLayout, to, offset, false, true)
                    }
                } catch (_: Exception) {
                }
            }
            // 添加动画完成回调，确保平滑过渡
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // 确保指示器最终位置正确
                    updateIndicatorPositionDirectly(tabLayout, to)
                }
            })
        }
        indicatorAnimator?.start()
    }

    /**
     * 根据滑动速度播放指示器动画
     * @param tabLayout TabLayout 实例
     * @param from 起始位置
     * @param to 目标位置
     */
    private fun animateIndicator(tabLayout: TabLayout, from: Int, to: Int) {
        // 温和地取消动画：避免在动画过程中强制停止
        if (indicatorAnimator?.isRunning == true) {
            indicatorAnimator?.cancel()
        }

        // 根据滑动速度选择动画时长
        val duration = when {
            scrollVelocity > VERY_FAST_SCROLL_THRESHOLD -> {
                // 极快滑动时使用极短动画时间，保持流畅感
                VERY_FAST_ANIMATION_DURATION
            }
            scrollVelocity > FAST_SCROLL_THRESHOLD -> {
                // 快速滑动时使用较短动画时间，避免卡顿
                (DRAG_ANIMATION_DURATION * FAST_ANIMATION_RATIO).toLong()
                    .coerceAtLeast(MIN_ANIMATION_DURATION)
            }
            else -> {
                // 正常 drag 使用专门的动画时间
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
                        // 不更新 TabLayout 内置选中文字样式，避免把自定义粗体还原
                        method.invoke(tabLayout, from, offset, false, true)
                    } else {
                        method.invoke(tabLayout, to, offset, false, true)
                    }
                } catch (_: Exception) {
                }
            }
            // 添加动画完成回调，确保平滑过渡
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // 确保指示器最终位置正确
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
            // 如果在 drag 过程中，忽略 tab 选中事件，避免强制跳转页面
            if (isDragging) return
            val target = tab.position
            (tab.parent?.parent as? BounceTabLayoutContainer)?.setSkipAnim(true)

            if (skipAnyAnim) {
                LogUtils.e("LiveMainTabMediator------->skipAnyAnim")
                viewPager.setCurrentItem(target, false)
                skipAnyAnim = false
            } else if (isTabClick) {
                // 点击时直接切换到对应页（避免跳错页）
                isClickAnimating = true
                viewPager.setCurrentItem(target, false)
                isTabClick = false
            } else {
                viewPager.doSmartAnim(targetPosition = target)
            }
            afterTabSelected?.invoke(target)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }
}