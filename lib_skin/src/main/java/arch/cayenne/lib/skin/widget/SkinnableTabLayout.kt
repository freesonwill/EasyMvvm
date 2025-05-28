package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTabLayoutHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SkinnableTabLayout : TabLayout {

    private val backgroundTintHelper = SkinnableBackGroundHelper(this)
    private val tabLayoutHelper = SkinnableTabLayoutHelper(this)
    private val TAG = SkinnableTabLayout::class.java.simpleName
    private val flowHelper = SkinnableViewFlowHelper()

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
        flowHelper.startSkinFlow {
            backgroundTintHelper.updateSkin()
            tabLayoutHelper.updateSkin()
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        tabLayoutHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun addTab(tab: Tab, position: Int, setSelected: Boolean) {
        super.addTab(tab, position, setSelected)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun addTab(tab: Tab, position: Int) {
        super.addTab(tab, position)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun addTab(tab: Tab, setSelected: Boolean) {
        super.addTab(tab, setSelected)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun addTab(tab: Tab) {
        super.addTab(tab)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        flowHelper.destroyFlow()
    }

}