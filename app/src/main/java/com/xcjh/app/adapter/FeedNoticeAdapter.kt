package com.xcjh.app.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.FeedBackBean
import com.xcjh.base_lib.utils.TimeUtil

class FeedNoticeAdapter: BaseQuickAdapter<FeedBackBean, QuickViewHolder>() {

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(R.layout.item_feed, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: FeedBackBean?) {
        // 设置item数据

        holder.getView<TextView>(R.id.tv1).text =item!!.title
        holder.getView<TextView>(R.id.tv2).text =item!!.notice
        holder.getView<TextView>(R.id.tvtime).text =
            TimeUtil.timeStamp2Date(item!!.createTime.toLong(),"yyyy-MM-dd HH:mm")
    }
}