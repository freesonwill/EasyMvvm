package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTabLayoutHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

class SkinnableTabLayout : TabLayout, ISkinnableBiz {
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
        flowHelper.startSkinFlow(findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundTintHelper.updateSkin()
            tabLayoutHelper.updateSkin()
        }

        flowHelper.startLanguageFlow(findViewTreeLifecycleOwner()?.lifecycleScope) {
            tabLayoutHelper.updateLanguage(it)
        }
    }

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
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
    /**
     * 在 TabLayout 尾部新增一個 Tab，並精準地向左滾動一小段距離以顯示它
     * @param tab 新增Tab
     * @param extraPaddingDp 額外向左滾動的 padding (DP 單位)
     */
    fun addTabAndScrollPrecisely(tab: Tab, extraPaddingDp: Int = 16) {
        addTab(tab) // 第二個參數 true 表示同時選中它
        post {
            val newTabView = tab.view

            // 3. 計算需要滾動的距離
            // 新 Tab 的右邊緣位置
            val tabRight = newTabView.right
            // TabLayout 的可見寬度
            val layoutWidth = width

            // 如果 Tab 的右邊緣超出了可見寬度
            if (tabRight > layoutWidth) {
                // 計算超出的距離
                val scrollAmount = tabRight - layoutWidth
                // 增加額外的 padding
                val extraPaddingPx = (extraPaddingDp * resources.displayMetrics.density).toInt()
                // 4. 執行平滑滾動
                smoothScrollBy(scrollAmount + extraPaddingPx, 0)
                post { tab.select() }

            }
        }
    }

    override fun addTab(tab: Tab) {
        super.addTab(tab)
        tabLayoutHelper.updateTabBackground(tab)
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }

    override fun setTintColorRes(resId: Int) {
        TODO("Not yet implemented")
    }

    override fun setForegroundRes(resId: Int) {
        TODO("Not yet implemented")
    }

    //设置Tab时添加tabResArray，语言切换时更新tab
    fun setTabResArray(tabResArray: IntArray){
        tabLayoutHelper.updateTabResArray(tabResArray)
    }

    override fun forceUpdateSkin() {
        backgroundTintHelper.updateSkin(SkinMsgType.SELF)
        tabLayoutHelper.updateSkin(SkinMsgType.SELF)
    }

}