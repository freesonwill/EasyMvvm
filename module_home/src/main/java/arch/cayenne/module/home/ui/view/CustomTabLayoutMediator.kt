package arch.cayenne.module.home.ui.view

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper
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

    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null


    // 實際執行 TabLayout 滾動的地方，用映射的方式叫用 animationTo 來達到 smoothScroll 效果
    private val doOnClick: (Int) -> Unit = { position ->
        try {
            val method = TabLayout::class.java.getDeclaredMethod("animateToTab", Int::class.java)
            method.isAccessible = true
            method.invoke(tabLayout, position)

            // 手動切換被選擇的 tab
            tabLayout.getTabAt(position)?.select()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun interface TabConfigurationStrategy {
        fun onConfigureTab(tab: TabLayout.Tab, position: Int)
    }

    fun attach(fakeViewPager: ImageView, onChangeFinished: ((position: Int) -> Unit)? = null) {
        if (attached) throw IllegalStateException("TabLayoutMediator is already attached")

        adapter = viewPager.adapter ?: throw IllegalStateException(
            "TabLayoutMediator attached before ViewPager2 has an adapter"
        )
        attached = true

        onPageChangeCallback = TabLayoutOnPageChangeCallback(tabLayout).also {
            viewPager.registerOnPageChangeCallback(it)
        }

        onTabSelectedListener = ViewPagerOnTabSelectedListener(viewPager, fakeViewPager, onChangeFinished).also {
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
                doOnClick(position)
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

    private class ViewPagerOnTabSelectedListener(
        private val viewPager: ViewPager2,
        private val fakeViewPager: ImageView,
        private val onChangeFinished: ((position: Int) -> Unit)?
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            ViewPagerAnimHelper().doDirectViewPagerAnim(
                targetPosition = tab.position,
                viewPager = viewPager,
                fakeViewPager = fakeViewPager
            )
            onChangeFinished?.invoke(tab.position)
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