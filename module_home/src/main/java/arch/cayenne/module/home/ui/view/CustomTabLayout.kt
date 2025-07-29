package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTabLayoutHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import com.google.android.material.tabs.TabLayout
import kotlin.math.hypot

class CustomTabLayout: TabLayout {
    private val TAG = SkinnableTabLayout::class.java.simpleName

    /** For SkinnableTabLayout Start */
    private val backgroundTintHelper = SkinnableBackGroundHelper(this)
    private val tabLayoutHelper = SkinnableTabLayoutHelper(this)
    private val flowHelper = SkinnableViewFlowHelper()
    /** For SkinnableTabLayout End */

    var onTabClick: ((Int) -> Unit)? = null

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow(findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundTintHelper.updateSkin()
            tabLayoutHelper.updateSkin()
        }
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

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        tabLayoutHelper.loadFromAttributes(attrs, defStyleAttr)
    }

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
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val dx = event.x - downX
                    val dy = event.y - downY
                    val distance = hypot(dx, dy)
                    if (distance < touchSlop) {
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
}