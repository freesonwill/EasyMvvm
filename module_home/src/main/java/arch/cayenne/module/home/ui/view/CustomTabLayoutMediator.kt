package arch.cayenne.module.home.ui.view

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper.Companion.getAnimHelper
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference

class CustomTabLayoutMediator(
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val autoRefresh: Boolean = true,
    private val tabConfigurationStrategy: TabConfigurationStrategy
) {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached = false
    private var skipAnyAnim = false

    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null

    /**
     * 執行 TabLayout 滾動到指定位置
     * 用映射的方式叫用 animationTo 或 setScrollPosition 來控制是否需要 smoothScroll 效果
     * @param position 目標位置
     * @param noTabAnim 是否不需要 TabLayout 的動畫效果，默認為 false
     * @param noViewPagerAnim 是否不需要動畫效果，默認為 false
     */
    private fun doOnClick(position: Int, noTabAnim: Boolean = false, noViewPagerAnim: Boolean = false) {
        try {
            if (!noTabAnim) {
                TabLayout::class.java
                    .getDeclaredMethod("animateToTab", Int::class.java)
                    .apply {
                        isAccessible = true
                        invoke(tabLayout, position)
                    }
            } else {
                // 如果是 BounceTabLayoutContainer，則跳過回彈動畫
                (tabLayout.parent as? BounceTabLayoutContainer)?.setSkipAnim(true)

                TabLayout::class.java
                    .getDeclaredMethod(
                        "setScrollPosition",
                        Int::class.java,
                        Float::class.java,
                        Boolean::class.java,
                        Boolean::class.java
                    ).apply {
                        isAccessible = true
                        invoke(tabLayout, position, 0f, true, false)
                    }

                // 清空之前的切換紀錄
                viewPager.getAnimHelper().resetHistory()
            }

            skipAnyAnim = noViewPagerAnim
            tabLayout.getTabAt(position)?.select()
        } catch (e: Exception) {
            e.printStackTrace()
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

        // 清空之前的切換紀錄
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
        if(tabLayout is CustomTabLayout) {
            // 設置自訂的 ClickListener
            tabLayout.onTabClick = { position ->
                doOnClick(position, noTabAnim = false, noViewPagerAnim = true)
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

    fun isAttached(): Boolean = attached

    fun selectTabWithoutAnimation(position: Int) {
        doOnClick(position = position, noTabAnim = true, noViewPagerAnim = true)
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

    private inner class TabLayoutOnPageChangeCallback(tabLayout: TabLayout) : ViewPager2.OnPageChangeCallback() {
        private val tabLayoutRef = WeakReference(tabLayout)
        private var previousScrollState = ViewPager2.SCROLL_STATE_IDLE
        private var scrollState = ViewPager2.SCROLL_STATE_IDLE

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state
            tabLayoutRef.get()?.let {
                try {
                    val method = TabLayout::class.java.getDeclaredMethod("updateViewPagerScrollState", Int::class.java)
                    method.isAccessible = true
                    method.invoke(it, scrollState)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // 移除 TabLayout 原生的 select tab 邏輯，避免觸發原生的滑動效果
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get() ?: return
            if (position != tabLayout.selectedTabPosition && position < tabLayout.tabCount) {
                // 設置自訂的滑動效果
                doOnClick(position)
            }
        }

        fun reset() {
            previousScrollState = ViewPager2.SCROLL_STATE_IDLE
            scrollState = ViewPager2.SCROLL_STATE_IDLE
        }
    }

    private inner class ViewPagerOnTabSelectedListener(
        private val viewPager: ViewPager2,
        private val afterTabSelected: ((position: Int) -> Unit)?
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            // 如果是 BounceTabLayoutContainer，則跳過回彈動畫
            (tab.parent?.parent as? BounceTabLayoutContainer)?.setSkipAnim(skipAnyAnim)

            if (skipAnyAnim) {
                viewPager.setCurrentItem(tab.position, false)
                skipAnyAnim = false
            } else {
                viewPager.doSmartAnim(targetPosition = tab.position)
            }
            afterTabSelected?.invoke(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    private inner class PagerAdapterObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = populateTabsFromPagerAdapter()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = populateTabsFromPagerAdapter()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) = populateTabsFromPagerAdapter()
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = populateTabsFromPagerAdapter()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = populateTabsFromPagerAdapter()
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = populateTabsFromPagerAdapter()
    }
}