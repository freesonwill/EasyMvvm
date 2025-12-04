package com.walisport.module.hall.data

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class UniversalLoadMoreScrollListener(
    private val threshold: Int = 1,     // 距离底部还有 6 个 item 就加载（不用管几列）
    private val onLoadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    private var isLoading = false

    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
        if (dy <= 0) return

        val layoutManager = rv.layoutManager ?: return
        val totalItemCount = rv.adapter?.itemCount ?: 0
        if (totalItemCount == 0) return

        val lastVisibleItemPosition = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()  // 强制用 Linear 的方法
            is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(null).last()
            else -> return
        }

        if (!isLoading && (totalItemCount - lastVisibleItemPosition) <= threshold) {
            isLoading = true
            rv.post {
                onLoadMore()
                isLoading = false
            }
        }
    }

    fun reset() { isLoading = false }
}