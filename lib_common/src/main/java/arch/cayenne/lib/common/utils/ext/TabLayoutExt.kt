package arch.cayenne.lib.common.utils.ext

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayout
import kotlin.math.cos
import kotlin.math.pow

/**
 * @date: 2025/5/30 11:55
 * @description: tabLayout扩展
 */
object TabLayoutExt {
    //设置tab之间的外边距
    fun TabLayout.reflexMargin(leftMargin: Int, rightMargin: Int, margin: Int) {
        val tabLayout = this
        val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until mTabStrip.childCount) {
            val tabView = mTabStrip.getChildAt(i)
            val params = tabView.layoutParams as LinearLayout.LayoutParams
            when (i) {
                0 -> {//第一个tab
                    params.leftMargin = leftMargin
                    params.rightMargin = margin
                }

                mTabStrip.childCount - 1 -> {//最后一个tab
                    params.leftMargin = margin
                    params.rightMargin = rightMargin
                }

                else -> {//中间tab
                    params.leftMargin = margin
                    params.rightMargin = margin
                }
            }
            tabView.layoutParams = params
        }
    }

    /**
     * 設定最後一個 tab 跟手動畫，ivMore/llMore 跟手縮放顯示
     * @param ivMore 一般更多按鈕
     * @param llMore 展開後的更多區塊
     * @param triggerRatio 幾成可見時開始動畫，預設0.8f
     */
    fun TabLayout.setupEndTabMoreAnimation(
        ivMore: View,
        llMore: View,
        triggerRatio: Float = 0.8f
    ) {
        val animate = fun() {
            if (this.tabCount <= 1) {
                ivMore.visibility = View.INVISIBLE
                llMore.visibility = View.INVISIBLE
                return
            }
            val tabStrip = getChildAt(0) as? LinearLayout ?: return
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1) ?: return
            val tabLayoutRect = IntArray(2)
            val lastTabRect = IntArray(2)
            getLocationOnScreen(tabLayoutRect)
            lastTab.getLocationOnScreen(lastTabRect)
            val tabLayoutRight = tabLayoutRect[0] + width
            val lastTabLeft = lastTabRect[0]
            val visibleWidth = (tabLayoutRight - lastTabLeft).coerceIn(0, lastTab.width)
            val ratio = if (lastTab.width > 0) visibleWidth.toFloat() / lastTab.width else 0f
            if (ratio.isNaN() || ratio.isInfinite()) return

            val progress = if (ratio > triggerRatio) {
                val linear = ((ratio - triggerRatio) / (1f - triggerRatio)).coerceIn(0f, 1f)
                val ease = ((1 - cos(linear * Math.PI)) / 2f).toFloat()
                ease.toDouble().pow(2.0).toFloat()
            } else {
                0f
            }
            ivMore.scaleX = 1f - 0.2f * progress
            ivMore.scaleY = 1f - 0.2f * progress
            ivMore.alpha = 1f - progress
            ivMore.visibility = if (progress < 0.95f) View.VISIBLE else View.INVISIBLE

            llMore.scaleX = (0.8f + 0.2f * progress).coerceAtMost(1.0f)
            llMore.scaleY = (0.8f + 0.2f * progress).coerceAtMost(1.0f)
            llMore.alpha = progress
            llMore.visibility = if (progress > 0.05f) View.VISIBLE else View.INVISIBLE
        }
        // 註冊 scroll 監聽
        viewTreeObserver.addOnScrollChangedListener(animate)
        post(animate)
    }

    fun TabLayout.selectTabWithoutAnimation(index: Int) {
        val tab = getTabAt(index) ?: return

        // 透過反射關掉 animateToTab
        try {
            val method = TabLayout::class.java.getDeclaredMethod("setScrollPosition", Int::class.java, Float::class.java, Boolean::class.java, Boolean::class.java)
            method.isAccessible = true
            method.invoke(this, index, 0f, true, false)
            tab.select()
        } catch (e: Exception) {
            e.printStackTrace()
            tab.select()
        }
    }
}