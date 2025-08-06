package com.walisport.module.search.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.walisport.module.search.R
import com.walisport.module.search.utils.FoldUtils
import kotlin.math.max

/**
 * @author: caomei
 * @date: 2025/4/22 14:18
 * @description: 折叠
 */
class HistoryFoldLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FlowListView(context, attrs, defStyleAttr) {
    private var upFoldView: View ?=null
    private var downFoldView:View ?=null
    private var canFold = false
    private var fold = false
    private var index = 0
    private var surplusWidth = 0

    init {
        upFoldView = LayoutInflater.from(context).inflate(R.layout.view_item_fold_up, null)
        downFoldView = LayoutInflater.from(context).inflate(R.layout.view_item_fold_down, null)
        upFoldView?.setOnClickListener { v: View? ->
            mFold = false
            flowAdapter.notifyDataChanged()
        }
        downFoldView?.setOnClickListener { v: View? ->
            mFold = true
            flowAdapter.notifyDataChanged()
        }
        setOnFoldChangedListener { canFold: Boolean, fold: Boolean, index: Int, surplusWidth: Int ->
            this.canFold = canFold
            this.fold = fold
            this.index = index
            this.surplusWidth = surplusWidth
            refreshFoldView()
        }
    }

    /**
     * isEditor true 是编辑  false不是
     */
    fun setEditor(isEditor: Boolean) {
        if (isEditor) {
            upFoldView?.visibility = INVISIBLE
            downFoldView?.visibility = INVISIBLE
            mFold = false
            flowAdapter.notifyDataChanged()
        } else {
            upFoldView?.visibility = VISIBLE
            downFoldView?.visibility = VISIBLE
            mFold = true
            flowAdapter.notifyDataChanged()
        }
    }

    override fun updateView() {
        super.updateView()
        refreshFoldView()
    }

    private fun refreshFoldView() {
        if(!canFold) return

        FoldUtils.removeFromParent(upFoldView!!)
        FoldUtils.removeFromParent(downFoldView!!)

        if (fold) {
            getIndex().let {
                if(it >= 0) addView(upFoldView, it)
            }
        } else {
            addView(downFoldView)
        }
    }

    /**
     * 計算按鈕的索引位置。
     * 如果剛好全部項目都能放下，回傳 -1（表示不用顯示按鈕）。
     */
    private fun getIndex(): Int {
        // 按鈕寬度
        val btnWidth = max(FoldUtils.getViewWidth(upFoldView!!), FoldUtils.getViewWidth(downFoldView!!))
        val maxWidth = measuredWidth    // 當前容器最大寬度
        val maxLine = mFoldLines        // 最多允許的行數
        var tempWidth = 0               // 累積當前行的總寬度
        var tempLine = 0                // 累積目前是第幾行（從 0 開始）
        var tempLineCount = 0           // 當前行放了幾個元素

        for(i in 0 until childCount) {
            val childView = getChildAt(i)
            val space = if (tempLineCount > 0) mHorizontalSpacing else 0
            val childWidth = FoldUtils.getViewWidth(childView) + space

            if(tempWidth + childWidth < maxWidth) {
                // 如果加上這個元素後還不會超過行寬，就放進當前行
                tempWidth += childWidth
                tempLineCount++
            } else {
                // 換行處理
                tempLine++
                if(tempLine >= maxLine) {
                    // 已達允許行數上限，不能再換行了
                    return when {
                        i == childCount -1 -> -1                // 剛好全部項目都能放下，不用顯示按鈕
                        tempWidth + btnWidth <= maxWidth -> i   // 如果當前行還放得下按鈕，就讓按鈕和前面項目共存
                        else -> i - 1                           // 否則回退一個元素，空出空間讓按鈕顯示
                    }
                } else {
                    // 開啟新的一行，重設行內寬度與計數
                    tempLineCount = 0
                    tempWidth = childWidth
                }
            }
        }

        // 所有元素都能顯示，無需折疊
        return -1
    }
}