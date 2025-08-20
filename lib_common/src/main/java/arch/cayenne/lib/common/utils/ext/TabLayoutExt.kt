package arch.cayenne.lib.common.utils.ext

import android.os.SystemClock
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
     */
    fun TabLayout.setupEndTabMoreAnimation(
        ivMore: View,
        llMore: View
    ) {
        var isExpanded = false
        var isAnimating = false
        var lastTabCount = 0
        var isInTransition = false
        var lockState = 0L
        var isInitialized = false
        var startupLock = 0L

        // 初始狀態：顯示 ivMore，隱藏 llMore
        ivMore.visibility = View.VISIBLE
        ivMore.alpha = 1f
        ivMore.scaleX = 1f
        ivMore.scaleY = 1f
        llMore.visibility = View.GONE
        llMore.alpha = 0f
        llMore.scaleX = 1f
        llMore.scaleY = 1f

        val updateUI = fun() {
            // 動畫進行中，跳過更新
            if (isAnimating) return

            // 未初始化完成，保持初始狀態
            if (!isInitialized) {
                ivMore.visibility = View.VISIBLE
                llMore.visibility = View.GONE
                return
            }

            // 初始鎖定，確保動畫不自動展開
            val currentTime = SystemClock.uptimeMillis()
            if (currentTime < startupLock) {
                ivMore.visibility = View.VISIBLE
                ivMore.alpha = 1f
                llMore.visibility = View.GONE
                return
            }

            // Tab 數量少，保持 ivMore 顯示
            if (this.tabCount <= 1) {
                ivMore.visibility = View.VISIBLE
                ivMore.alpha = 1f
                llMore.visibility = View.GONE
                return
            }

            val tabStrip = getChildAt(0) as? LinearLayout ?: return
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1) ?: return

            if (lastTab.width == 0) return

            // 使用螢幕位置檢測 tab 可見性
            val lastTabWidth = lastTab.width
            val tabLayoutRect = IntArray(2)
            getLocationOnScreen(tabLayoutRect) // TabLayout 在螢幕上的位置
            val lastTabRect = IntArray(2)
            lastTab.getLocationOnScreen(lastTabRect) // Last Tab 在螢幕上的位置

            val tabLayoutRightEdge = tabLayoutRect[0] + width
            val lastTabLeftEdge = lastTabRect[0]
            val lastTabRightEdge = lastTabRect[0] + lastTabWidth

            // 判斷 lastTab 是否完全在 TabLayout 的可視範圍內
            val isLastTabFullyVisible = lastTabLeftEdge >= tabLayoutRect[0] &&
                    lastTabRightEdge <= tabLayoutRightEdge &&
                    lastTabWidth > 0 // 確保 tab 有寬度

            // 展開：最後一個 tab 完全可見時觸發
            val shouldExpand = isLastTabFullyVisible && !isExpanded && !isInTransition

            // 收起：最後一個 tab 不再完全可見時觸發
            val shouldCollapse = !isLastTabFullyVisible && isExpanded && !isInTransition

            val hasNewTabAdded = tabCount > lastTabCount

            // 更新追蹤的 tab 數量
            lastTabCount = tabCount

            // 檢測是否為超寬 tab
            val averageTabWidth = tabStrip.width / tabCount.toFloat()
            val isExtraWideTab = lastTabWidth > averageTabWidth * 1.5f

            // 檢查是否在鎖定期間
            val now = SystemClock.uptimeMillis()
            val isLocked = now < lockState

            // 新增 tab 時啟動鎖定，避免閃爍
            if (hasNewTabAdded) {
                val lockDuration = if (isExtraWideTab) 800L else 500L
                lockState = now + lockDuration
                return
            }

            // 鎖定期間，不改變 UI 狀態
            if (isLocked) {
                return
            }

            // 展開邏輯：滑動到末端時顯示 llMore
            if (shouldExpand) {
                isExpanded = true
                isInTransition = true
                isAnimating = true

                ivMore.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(android.view.animation.AnticipateInterpolator(1.0f))
                    .withEndAction {
                        ivMore.visibility = View.GONE
                    }
                    .start()

                llMore.alpha = 0f
                llMore.scaleX = 0.8f
                llMore.scaleY = 0.8f
                llMore.visibility = View.VISIBLE
                llMore.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(250)
                    .setInterpolator(android.view.animation.OvershootInterpolator(1.0f))
                    .withEndAction {
                        isInTransition = false
                        isAnimating = false
                    }
                    .start()

            } else if (shouldCollapse) {
                // 收起邏輯：離開末端時顯示 ivMore
                isExpanded = false
                isInTransition = true
                isAnimating = true

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
                        isAnimating = false
                    }
                    .start()
            }
        }

        // 註冊滾動監聽
        viewTreeObserver.addOnScrollChangedListener(updateUI)

        // 延遲初始化，避免在佈局過程中觸發
        post {
            // 初始化完成標記
            lastTabCount = tabCount
            isInitialized = true
            // 初始鎖定 500ms，避免進頁會觸發展開
            startupLock = SystemClock.uptimeMillis() + 500L
        }
    }
}