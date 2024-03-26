package com.xcjh.app.adapter

import android.content.Context
import android.view.View
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



        holder.getView<TextView>(R.id.tvtime).text =
            TimeUtil.timeStamp2Date(item!!.createTime.toLong(),"yyyy-MM-dd HH:mm")
        when(item.type){////通知类型(0系统通知 1反馈结果 2禁言通知 3解禁通知)
            0->{
                holder.getView<TextView>(R.id.tv1).text =item!!.title
                holder.getView<TextView>(R.id.tv2).visibility= View.VISIBLE
                holder.getView<TextView>(R.id.tv2).text =item!!.notice
            }
            1->{
                holder.getView<TextView>(R.id.tv2).visibility= View.VISIBLE
                holder.getView<TextView>(R.id.tv1).text =item!!.title
                holder.getView<TextView>(R.id.tv2).text =context.resources.getString(R.string.txt_feedresult)+": "+item!!.notice
            }
            2->{
                holder.getView<TextView>(R.id.tv2).visibility= View.VISIBLE
                holder.getView<TextView>(R.id.tv1).text =item!!.title
                holder.getView<TextView>(R.id.tv2).text =context.resources.getString(R.string.txt_yuanyin)+": "+item!!.notice
            }
            3->{
                holder.getView<TextView>(R.id.tv2).visibility= View.GONE
                holder.getView<TextView>(R.id.tv1).text =item!!.title
            }
            4->{
                holder.getView<TextView>(R.id.tv1).text =item!!.title
                holder.getView<TextView>(R.id.tv2).visibility= View.VISIBLE
                holder.getView<TextView>(R.id.tv2).text =context.resources.getString(R.string.txt_yuanyin)+": "+item!!.notice
            }

        }
    }
}