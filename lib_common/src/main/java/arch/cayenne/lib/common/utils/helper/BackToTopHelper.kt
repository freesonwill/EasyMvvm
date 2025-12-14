package arch.cayenne.lib.common.utils.helper

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

/**
 * 回到頂部按鈕輔助類
 *
 * 用於在 RecyclerView 滾動時自動顯示/隱藏回到頂部按鈕，
 * 並提供平滑滾動回到頂部的功能。
 *
 * @param targetRecyclerView 目標 RecyclerView
 * @param button 回到頂部按鈕
 *
 * 使用範例：
 * ```kotlin
 * BackToTopHelper(recyclerView, backToTopButton)
 * ```
 */
class BackToTopHelper(
    val targetRecyclerView: RecyclerView,
    val button: AppCompatImageView,
    val isGridView: Boolean
) {
    private var totalDy = 0

    init {
        // 為目標 RecyclerView 新增一個滾動監聽器
        targetRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!button.isVisible) return
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    button.alpha = 1.0f
                } else {
                    button.alpha = 0.3f
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 累計垂直滾動的距離
                totalDy += dy

                // 獲取 RecyclerView 本身的高度，這通常可以視為「一頁」的高度
                val recyclerViewHeight = recyclerView.height

                // 檢查滾動距離是否超過了一頁的高度，並且按鈕當前是隱藏的
                if (totalDy > recyclerViewHeight && !button.isVisible) {
                    // 如果是，則顯示回到頂部的按鈕 (例如使用淡入動畫)
                    button.visibility = View.VISIBLE
                    button.alpha = 0.3f
                }
                // 檢查滾動距離是否已經小於一頁的高度，並且按鈕當前是顯示的
                else if (totalDy <= recyclerViewHeight && button.isVisible) {
                    // 如果是，則隱藏按鈕 (例如使用淡出動畫)
                    button.visibility = View.GONE
                }
            }
        })

        button.clickNoRepeat {
            //列表数据太多情况下点击返回顶部按钮需先跳至前几页再平滑滚动
            val number = if (isGridView) 36 else 8
            if (targetRecyclerView.adapter!!.itemCount > number) {
                targetRecyclerView.scrollToPosition(number)
            }
            targetRecyclerView.smoothScrollToPosition(0)
            button.visibility = View.GONE
        }
    }
}

