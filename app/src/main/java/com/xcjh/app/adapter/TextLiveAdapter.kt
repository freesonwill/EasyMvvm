package com.xcjh.app.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.LiveTextBean
import com.xcjh.base_lib.utils.view.visibleOrGone
import com.xcjh.base_lib.utils.view.visibleOrInvisible

class TextLiveAdapter : BaseQuickAdapter<LiveTextBean, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_text_live, parent)
    }

    private var homeLogo: String?=""
    private var awayLogo: String?=""

    fun setLogo(homeLogo: String?, awayLogo: String?) {
        this.homeLogo = homeLogo
        this.awayLogo = awayLogo
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: LiveTextBean?) {
        if (item != null) {
            holder.getView<TextView>(R.id.tv_content).text = /*item.time + "” " + */item.data
            val ivType = holder.getView<ImageView>(R.id.iv_type)
            val ivLogo = holder.getView<ImageView>(R.id.iv_logo)
            val lineView = holder.getView<View>(R.id.lineView)
            //事件发生方，0-中立、1-主队、2-客队
            ivLogo.visibleOrInvisible(item.position != 0)
            when (item.position) {
                1 -> {
                    Glide.with(context).load(homeLogo).placeholder(R.drawable.default_team_logo).into(ivLogo)
                }
                2 -> {
                    Glide.with(context).load(awayLogo).placeholder(R.drawable.default_team_logo).into(ivLogo)
                }
            }
            when (item.type) {
                //技术统计类型：  1-进球  2-角球  3-黄牌  4-红牌  5-越位  
                // 6-任意球  7-球门球  8-点球  9-换人  10-比赛开始  
                // 11-中场  12-结束  13-半场比分  15-两黄变红  
                // 16-点球未进  17-乌龙球  18-助攻  19-伤停补时  
                // 21-射正  22-射偏  23-进攻  24-危险进攻  25-控球率  
                // 26-加时赛结束  27-点球大战结束  28-VAR(视频助理裁判)  
                // 29-点球(点球大战)  30-点球未进(点球大战)
                1, 6, 7, 8, 16, 17 -> {
                    ivType.setImageResource(R.drawable.football)
                }
                3 ->
                    ivType.setImageResource(R.drawable.yellow_card)
                4, 15 ->
                    ivType.setImageResource(R.drawable.red_card)
                else -> {
                    ivType.setImageResource(R.drawable.default_txt_placeholder)
                }
            }
            //隐藏最后的进度线
            lineView.visibleOrGone(holder.absoluteAdapterPosition != items.size - 1)
        }
    }
}