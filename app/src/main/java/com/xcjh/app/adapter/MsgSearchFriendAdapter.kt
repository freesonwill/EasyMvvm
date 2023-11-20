package com.xcjh.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.FriendListBean
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.startNewActivity
import com.xcjh.base_lib.utils.view.CircleImageView

class MsgSearchFriendAdapter : BaseQuickAdapter<FriendListBean, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(R.layout.item_msgfriend, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: FriendListBean?) {
        Glide.with(context).load(item?.head).placeholder(R.drawable.default_anchor_icon).
        into(holder.getView<CircleImageView>(R.id.ivhead))
        holder.getView<TextView>(R.id.tvname).text =item?.nickName
        // 设置item数据
        holder.getView<View>(R.id.lltItem).setOnClickListener {

            startNewActivity<ChatActivity>() {
                if (item?.anchorId?.isNotEmpty() == true) {
                    this.putExtra(Constants.USER_ID, item?.anchorId)
                } else {
                    this.putExtra(Constants.USER_ID, "")
                }
                if (item?.nickName?.isNotEmpty() == true) {
                    this.putExtra(Constants.USER_NICK, item?.nickName)
                } else {
                    this.putExtra(Constants.USER_NICK, "")
                }
                if (item?.head?.isNotEmpty() == true) {
                    this.putExtra(Constants.USER_HEAD, item?.head)
                } else {
                    this.putExtra(Constants.USER_HEAD, "")
                }

            }
        }


    }
}