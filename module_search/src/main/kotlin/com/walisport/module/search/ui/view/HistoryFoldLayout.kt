package com.walisport.module.search.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.walisport.module.search.R
import com.walisport.module.search.utils.FoldUtils

/**
 * @author: caomei
 * @date: 2025/4/22 14:18
 * @description: 折叠
 */
class HistoryFoldLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FlowListView(context, attrs, defStyleAttr) {
    private var upFoldView: View ?=null
    private var downFoldView:View ?=null
    private var canFold = false
    private var fold = false
    private var index = 0
    private var surplusWidth = 0

    init {
        upFoldView  = LayoutInflater.from(context).inflate(R.layout.view_item_fold_up, null)
        downFoldView  = LayoutInflater.from(context).inflate(R.layout.view_item_fold_down, null)



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
    fun setEditor(isEditor:Boolean){
        if(isEditor){
            upFoldView?.visibility= INVISIBLE
            downFoldView?.visibility= INVISIBLE
            mFold = false
            flowAdapter.notifyDataChanged()
        }else{
            upFoldView?.visibility= VISIBLE
            downFoldView?.visibility= VISIBLE
            mFold = true
            flowAdapter.notifyDataChanged()
        }

    }

    override fun updateView() {
        super.updateView()
        refreshFoldView()
    }

    private fun refreshFoldView() {
        FoldUtils.removeFromParent(upFoldView!!)
        FoldUtils.removeFromParent(downFoldView!!)
        if (canFold) {
            addView(downFoldView)
            if (fold) {
                FoldUtils.removeFromParent(upFoldView!!)
                val upIndex = index(index, surplusWidth)
                addView(upFoldView, upIndex)
            } else {
                FoldUtils.removeFromParent(downFoldView!!)
                addView(downFoldView)
            }
        }
    }

    private fun index(index: Int, surplusWidth: Int): Int {
        var upIndex = index
        var upWidth: Int = FoldUtils.getViewWidth(upFoldView!!)
        //当剩余空间大于等于展开View宽度直接加入index+1
        if (surplusWidth >= upWidth) {
            upIndex = index + 1
        } else { //找到对应的位置
            for (i in index downTo 0) {
                val view = getChildAt(i)
                val viewWidth: Int = FoldUtils.getViewWidth(view)
                upWidth -= viewWidth
                if (upWidth <= 0) {
                    upIndex = i
                    break
                }
            }
        }
        return upIndex
    }
}