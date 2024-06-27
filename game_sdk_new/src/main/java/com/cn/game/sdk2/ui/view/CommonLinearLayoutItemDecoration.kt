package com.cn.game.sdk2.ui.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class CommonLinearLayoutItemDecoration(
    //顶部
     private val top: Int = 0,
    //底部
     private val bottom: Int = 0,
    //顶部
     private val start: Int = 0,
    //底部
     private val end: Int = 0,
    //垂直间隔
     private val spacingV: Int = 0
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        //这里是关键，需要根据你有几列来判断
        val position = parent.getChildAdapterPosition(view) // item position

        if ((parent.layoutManager as LinearLayoutManager?)!!.orientation == LinearLayoutManager.VERTICAL) {
            if (position == 0) {
                if (top != 0) {
                    outRect.top = top
                }
            } else {
                if (spacingV != 0) {
                    outRect.top = spacingV
                }
            }
            outRect.left = start
            outRect.right = end
            if (position == parent.adapter!!.itemCount - 1 && bottom != 0) {
                outRect.bottom = bottom
            }
        } else {
            if (position == 0) {
                if (start != 0) {
                    outRect.left = start
                }
            } else {
                if (spacingV != 0) {
                    outRect.left = spacingV
                }
            }
            outRect.top = top
            outRect.bottom = bottom
            if (position == parent.adapter!!.itemCount - 1 && end != 0) {
                outRect.right = end
            }
        }
    }
}