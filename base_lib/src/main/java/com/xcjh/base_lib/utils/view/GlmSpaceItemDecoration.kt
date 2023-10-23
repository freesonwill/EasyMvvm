package com.xcjh.base_lib.utils.view

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 单布局有用  多布局得特殊处理
 * GridLayoutManager
 * paddingH   左右内边距
 * paddingV   上下内边距
 * margin    外边距在RecyclerView中设置padding来实现

 */
class GlmSpaceItemDecoration(
    private val spanCount: Int,
    private val paddingH: Int = 0,// 左右内边距
    private val paddingV: Int = 0,// 上下内边距
) : RecyclerView.ItemDecoration() {


    @SuppressLint("WrongConstant")
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        /**最left列      right
         * 0   1   2   3      最top行
         * 4   5   6   7
         * 8   9            最bottom行
         */
        //左右间隔个数 = spanCount-1
        //左右间隔距离 = paddingH
        //总共左右间隔距离 = 左右间隔个数 * 左右间隔距离
        //每个item左右间隔距离 = 总共左右间隔 /spanCount
        val position = parent.getChildAdapterPosition(view)
        val column: Int = position % spanCount //列
        val row: Int = position / spanCount //行

        //使得每个outRect宽度相等
        outRect.left = column * paddingH / spanCount  //
        outRect.right = paddingH - (column + 1) * paddingH / spanCount
        if (position >= spanCount) {//除第一行外 加底边距
            outRect.top = paddingV
        }
    }


}

