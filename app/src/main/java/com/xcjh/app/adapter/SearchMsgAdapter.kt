package com.xcjh.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.MsgListBean
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.feed.FeedNoticeActivity
import com.xcjh.app.ui.room.MsgListNewData
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.startNewActivity
import com.xcjh.base_lib.utils.view.CircleImageView

class SearchMsgAdapter : BaseQuickAdapter<MsgListNewData, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(R.layout.item_search_msg, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: MsgListNewData?) {
        // 设置item数据
        when(item?.msgType){//	消息类型(0:text、1:image、2:voice、3:video、4:music、5:news)
            0->{
                holder.getView<TextView>(R.id.tvconetnt).text =item!!.content
            }
            1->{
                holder.getView<TextView>(R.id.tvconetnt).text =context.resources.getString(R.string.txt_msg_pic)
            }
            else->{
                holder.getView<TextView>(R.id.tvconetnt).text =item!!.content
            }

        }
        holder.getView<TextView>(R.id.tvname).text = item!!.nick
        // holder.getView<TextView>(R.id.tvconetnt).text = item!!.content
        Glide.with(context).load(item.avatar).placeholder(R.drawable.icon_avatar)
            .into(holder.getView<CircleImageView>(R.id.ivhead))
        holder.itemView.setOnClickListener {

            item.noReadSum = 0
            notifyItemChanged(position)
            startNewActivity<ChatActivity>() {
                if (item?.anchorId?.isNotEmpty() == true) {
                    this.putExtra(Constants.USER_ID, item?.anchorId)
                } else {
                    this.putExtra(Constants.USER_ID, "")
                }
                if (item?.nick?.isNotEmpty() == true) {
                    this.putExtra(Constants.USER_NICK, item?.nick)
                } else {
                    this.putExtra(Constants.USER_NICK, "")
                }
                if (item?.avatar?.isNotEmpty() == true) {
                    this.putExtra(Constants.USER_HEAD, item?.avatar)
                } else {
                    this.putExtra(Constants.USER_HEAD, "")
                }

            }


        }
    }
}