package arch.cayenne.lib.common.utils.ext

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayout

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
        llMore: View
    ) {
        var isExpanded = false // 用一個狀態來表示是否已展開
        var isInTransition = false // 防止動畫重疊

        val animate = fun() {

            val tabStrip = getChildAt(0) as? LinearLayout ?: return
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1) ?: return

            if (lastTab.width == 0) return // 避免 lastTab 寬度為0時的除零錯誤

            val tabLayoutRect = IntArray(2)
            getLocationOnScreen(tabLayoutRect) // TabLayout 在螢幕上的位置
            val lastTabRect = IntArray(2)
            lastTab.getLocationOnScreen(lastTabRect) // Last Tab 在螢幕上的位置

            val tabLayoutRightEdge = tabLayoutRect[0] + width
            val lastTabLeftEdge = lastTabRect[0]
            val lastTabRightEdge = lastTabRect[0] + lastTab.width

            // 判斷 lastTab 是否完全在 TabLayout 的可視範圍內
            val isLastTabFullyVisible = lastTabLeftEdge >= tabLayoutRect[0] &&
                    lastTabRightEdge <= tabLayoutRightEdge &&
                    lastTab.width > 0 // 確保 tab 有寬度

            // 條件1: Last tab 完全可見，且尚未展開，且不在動畫中
            if (isLastTabFullyVisible && !isExpanded && !isInTransition) {
                isExpanded = true
                isInTransition = true

                // 執行展開動畫完成顯示 llMore，隱藏 ivMore
                ivMore.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(android.view.animation.AnticipateInterpolator(1.0f)) // 先收縮再消失
                    .withEndAction {
                        ivMore.visibility = View.GONE
                    }
                    .start()

                llMore.alpha = 0f
                llMore.scaleX = 0.8f // 從收縮狀態開始
                llMore.scaleY = 0.8f
                llMore.visibility = View.VISIBLE
                llMore.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(250)
                    .setInterpolator(android.view.animation.OvershootInterpolator(1.0f)) // 輕微超出再回來
                    .withEndAction {
                        isInTransition = false
                    }
                    .start()

                // 條件2: Last tab 不再完全可見（開始滑出），且已經展開，且不在動畫中
            } else if (!isLastTabFullyVisible && isExpanded && !isInTransition) {
                isExpanded = false
                isInTransition = true

                // 執行收回動畫完成顯示 ivMore，隱藏 llMore
                llMore.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(android.view.animation.AnticipateInterpolator(1.0f))
                    .withEndAction {
                        llMore.visibility = View.GONE
                    }
                    .start()

                ivMore.visibility = View.VISIBLE
                ivMore.alpha = 0f
                ivMore.scaleX = 0.8f
                ivMore.scaleY = 0.8f
                ivMore.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(250)
                    .setInterpolator(android.view.animation.OvershootInterpolator(1.0f))
                    .withEndAction {
                        isInTransition = false
                    }
                    .start()
            }
        }

        // 註冊 scroll 監聽
        viewTreeObserver.addOnScrollChangedListener(animate)
        post(animate)
    }
}