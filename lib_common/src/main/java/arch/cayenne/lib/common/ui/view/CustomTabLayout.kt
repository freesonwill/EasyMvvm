package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import arch.cayenne.lib.skin.widget.helper.SkinnableTabLayoutHelper
import kotlin.math.hypot

class CustomTabLayout : SkinnableTabLayout {
    private val TAG = SkinnableTabLayout::class.java.simpleName

    /** For SkinnableTabLayout Start */
    private val tabLayoutHelper = SkinnableTabLayoutHelper(this)

    /** For SkinnableTabLayout End */

    var onTabClick: ((Int) -> Unit)? = null

    // 滑動狀態追蹤
    private var isScrolling = false
    private var scrollStartTime = 0L
    private val scrollThreshold = 150L // 滑動閥值（毫秒）

    // 預選中狀態追蹤
    private var preSelectedPosition = -1
    private var isPreSelecting = false

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun addTab(tab: Tab, position: Int, setSelected: Boolean) {
        super.addTab(tab, position, setSelected)
        disableTabClick(tab)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun addTab(tab: Tab, position: Int) {
        super.addTab(tab, position)
        disableTabClick(tab)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun addTab(tab: Tab, setSelected: Boolean) {
        super.addTab(tab, setSelected)
        disableTabClick(tab)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun addTab(tab: Tab) {
        super.addTab(tab)
        disableTabClick(tab)
        tabLayoutHelper.updateTabBackground(tab)
    }

    // 攔截點擊事件，並將事件重送至自訂 onTabClick ，避免觸發 TabLayout 原本內建的滑動方法
    @SuppressLint("ClickableViewAccessibility")
    private fun disableTabClick(tab: Tab) {
        val tabView = getTabView(tab) ?: return

        var downX = 0f
        var downY = 0f
        val touchSlop = ViewConfiguration.get(tabView.context).scaledTouchSlop

        tabView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    scrollStartTime = System.currentTimeMillis()
                    isScrolling = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - downX
                    val dy = event.y - downY
                    val distance = hypot(dx, dy)

                    // 移動距離超過閥值則記為滑動狀態
                    if (distance > touchSlop) {
                        isScrolling = true
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val dx = event.x - downX
                    val dy = event.y - downY
                    val distance = hypot(dx, dy)
                    val timeElapsed = System.currentTimeMillis() - scrollStartTime

                    if (distance < touchSlop && timeElapsed < scrollThreshold && !isScrolling) {
                        onTabClick?.invoke(tab.position)
                    }
                    true
                }

                else -> true
            }
        }
    }

    private fun getTabView(tab: Tab): View? {
        val index = tab.position
        if (index < 0 || index >= tabCount) return null
        val tabStrip = getChildAt(0) as? ViewGroup ?: return null
        return tabStrip.getChildAt(index)
    }

    /**
     * 重置滑動狀態
     */
    fun resetScrollState() {
        isScrolling = false
        scrollStartTime = 0L
    }

    /**
     * 設置預選中狀態
     */
    fun setPreSelectedPosition(position: Int) {
        if (position != preSelectedPosition) {
            preSelectedPosition = position
            isPreSelecting = position != -1
            updatePreSelectionVisual()
        }
    }

    /**
     * 更新預選中狀態
     */
    private fun updatePreSelectionVisual() {
        // 清除所有tab的預選中狀態
        for (i in 0 until tabCount) {
            val tab = getTabAt(i)
            tab?.let {
                val tabView = getTabView(it)
                tabView?.let { view ->
                    // 重置所有tab的視覺效果
                    view.alpha = 1.0f
                    view.scaleX = 1.0f
                    view.scaleY = 1.0f
                }
            }
        }
    }
}