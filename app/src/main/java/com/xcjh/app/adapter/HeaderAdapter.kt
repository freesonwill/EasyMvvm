package com.xcjh.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseSingleItemAdapter
import com.xcjh.app.R

class HeaderAdapter: BaseSingleItemAdapter<Any, HeaderAdapter.VH>() {

    class VH(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.head_view, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: Any?) {

    }
    override fun isFullSpanItem(itemType: Int): Boolean {
        // 使用GridLayoutManager时，此类型的 item 是否是满跨度
        return true
    }
}