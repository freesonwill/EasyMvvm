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
        when (item!!.feedbackType) {
            "1" -> holder.getView<TextView>(R.id.tv11).text =context.resources.getString(R.string.txt_feedtype1)
            "2" -> holder.getView<TextView>(R.id.tv11).text =context.resources.getString(R.string.txt_feedtype2)
            "3" -> holder.getView<TextView>(R.id.tv11).text =context.resources.getString(R.string.txt_feedtype3)
            "4" -> holder.getView<TextView>(R.id.tv11).text =context.resources.getString(R.string.txt_feedtype4)
            "5" -> holder.getView<TextView>(R.id.tv11).text =context.resources.getString(R.string.txt_feedtype5)
            "6" -> holder.getView<TextView>(R.id.tv11).text =context.resources.getString(R.string.txt_feedtype6)
        }
        holder.getView<TextView>(R.id.tv22).text =item!!.feedbackContent
        holder.getView<TextView>(R.id.tv33).text =item!!.feedbackResult
        holder.getView<TextView>(R.id.tv44).text =
            TimeUtil.timeStamp2Date(item!!.feedbackTime.toLong(),"yyyy-MM-dd HH:mm")
    }
}