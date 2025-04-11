package com.walisport.module.bet.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BetSheetDecoration(private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 只為中間的項目設定間隔
        val position = parent.getChildAdapterPosition(view) // 獲取當前項目的位置

        // 判斷是否是第一個項目
        if (position != 0) {
            outRect.top = space // 項目上方設置間隔
        }

        // 判斷是否是最後一個項目
        if (position != state.itemCount - 1) {
            outRect.bottom = space // 項目下方設置間隔
        }
    }
}