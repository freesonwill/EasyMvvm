package com.walisport.module.live.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearSpacingItemDecoration(
    private val spacing: Int,         // 常规间距大小（像素）
    private val bottomSpacing: Int,   // 最后一个 item 与底部的距离（像素）
    private val includeEdge: Boolean = false // 是否包含顶部和左右边距
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item 位置
        val itemCount = parent.adapter?.itemCount ?: 0 // 总 item 数
        if (includeEdge) {
            // 包含边缘的情况
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
            outRect.left = spacing
            outRect.right = spacing
        } else {
            // 不包含边缘，只设置 item 之间的间距和底部间距
            if (position > 0) {
                outRect.top = spacing // 第一个 item 上面没有间距
            }
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else 0
            outRect.left = 0
            outRect.right = 0
        }
    }
}