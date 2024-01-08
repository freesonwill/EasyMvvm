package com.xcjh.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.MsgListNewData
import com.xcjh.app.utils.ChatTimeUtile
import com.xcjh.base_lib.utils.view.CircleImageView

class MsgListAdapter: BaseQuickAdapter<MsgListNewData, QuickViewHolder>() {

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(R.layout.item_msglist, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: MsgListNewData?) {
        // 设置item数据

        when(item?.noReadSum){
            0->{
                holder.getView<TextView>(R.id.tvnums1).visibility=View.GONE
                holder.getView<TextView>(R.id.tvnums2).visibility=View.GONE
            }
            in 1..9->{
                holder.getView<TextView>(R.id.tvnums1).visibility=View.VISIBLE
                holder.getView<TextView>(R.id.tvnums2).visibility=View.GONE
                holder.getView<TextView>(R.id.tvnums1).text = item!!.noReadSum.toString()
            }
            else->{
                holder.getView<TextView>(R.id.tvnums1).visibility=View.GONE
                holder.getView<TextView>(R.id.tvnums2).visibility=View.VISIBLE
                holder.getView<TextView>(R.id.tvnums2).text = item!!.noReadSum.toString()
            }

        }
        if (item.sent==2){//发送失败
            holder.getView<ImageView>(R.id.ivfaile).visibility=View.VISIBLE
        }else{
            holder.getView<ImageView>(R.id.ivfaile).visibility=View.GONE
        }
        if (item.dataType==2){//反馈通知
            holder.getView<TextView>(R.id.tvname).text =context.resources.getString(R.string.txt_feedtitle)
            holder.getView<TextView>(R.id.tvcontent).text =item!!.content
            Glide.with(context)
                .load(R.drawable.ic_notify)
                .into(holder.getView<CircleImageView>(R.id.ivhead))

        }else{
            when(item?.msgType){//	消息类型(0:text、1:image、2:voice、3:video、4:music、5:news)
                0->{
                    holder.getView<TextView>(R.id.tvcontent).text =item!!.content
                }
                1->{
                    holder.getView<TextView>(R.id.tvcontent).text =context.resources.getString(R.string.txt_msg_pic)
                }
                else->{
                    holder.getView<TextView>(R.id.tvcontent).text =item!!.content
                }

            }
            holder.getView<TextView>(R.id.tvname).text =item!!.nick
            Glide.with(context).load(item.avatar).placeholder(R.drawable.default_anchor_icon).into(holder.getView<CircleImageView>(R.id.ivhead))
        }
        val time: String = ChatTimeUtile.formatTimestamp(context,
            item.createTime
        )!!
        holder.getView<TextView>(R.id.tvtime).text =time


    }
}