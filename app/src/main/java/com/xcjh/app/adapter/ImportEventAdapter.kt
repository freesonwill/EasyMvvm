package com.xcjh.app.adapter

import com.xcjh.app.R
import com.xcjh.app.bean.IncidentsBean
import com.xcjh.app.bean.LiveTextBean
import com.xcjh.app.databinding.ItemImportEventBinding
import com.xcjh.base_lib.utils.view.visibleOrGone

class ImportEventAdapter : BaseViewBindingQuickAdapter<IncidentsBean, ItemImportEventBinding>() {


    override fun onBindViewHolder(
        holder: VH<ItemImportEventBinding>,
        position: Int,
        item: IncidentsBean?,
    ) {
        if (item != null) {
            val binding = holder.binding as ItemImportEventBinding
            binding.tvTime.text = item.time.toString()
            binding.topLine.visibleOrGone(holder.absoluteAdapterPosition != 0)
            binding.bottomLine.visibleOrGone(holder.absoluteAdapterPosition != items.size - 1)
            when (item.type) {
                //技术统计类型：  1-进球  2-角球  3-黄牌  4-红牌  5-越位  
                // 6-任意球  7-球门球  8-点球  9-换人  10-比赛开始  
                // 11-中场  12-结束  13-半场比分  15-两黄变红  
                // 16-点球未进  17-乌龙球  18-助攻  19-伤停补时  
                // 21-射正  22-射偏  23-进攻  24-危险进攻  25-控球率  
                // 26-加时赛结束  27-点球大战结束  28-VAR(视频助理裁判)  
                // 29-点球(点球大战)  30-点球未进(点球大战)
                1 -> {
                    //进球
                    binding.vHomeCard.visibleOrGone(false)
                    binding.vHomeShot.visibleOrGone(item.position == 1)
                    binding.vHomeExchange.visibleOrGone(false)
                    binding.vAwayCard.visibleOrGone(false)
                    binding.vAwayShot.visibleOrGone(item.position == 2)
                    binding.vAwayExchange.visibleOrGone(false)

                    binding.tvHomeShotScore.text = "${item.homeScore} - ${item.awayScore}"
                    binding.tvHomeShotMsg.text = item.playerName
                    binding.tvAwayShotScore.text = "${item.homeScore} - ${item.awayScore}"
                    binding.tvAwayShotMsg.text =  item.playerName
                }
                3, 4 ,15-> {
                    //红黄牌
                    binding.vHomeCard.visibleOrGone(item.position == 1)
                    binding.vHomeShot.visibleOrGone(false)
                    binding.vHomeExchange.visibleOrGone(false)
                    binding.vAwayCard.visibleOrGone(item.position == 2)
                    binding.vAwayShot.visibleOrGone(false)
                    binding.vAwayExchange.visibleOrGone(false)
                    binding.tvHomeMsg.text = item.playerName
                    binding.ivHomeIcon.setImageResource(if (item.type == 3) R.drawable.yellow_card else R.drawable.red_card)
                    binding.tvAwayMsg.text =item.playerName
                    binding.ivAwayIcon.setImageResource(if (item.type == 3) R.drawable.yellow_card else R.drawable.red_card)
                }
                9 -> {
                    //换人
                    binding.vHomeCard.visibleOrGone(false)
                    binding.vHomeShot.visibleOrGone(false)
                    binding.vHomeExchange.visibleOrGone(item.position == 1)
                    binding.vAwayCard.visibleOrGone(false)
                    binding.vAwayShot.visibleOrGone(false)
                    binding.vAwayExchange.visibleOrGone(item.position == 2)

                    binding.tvHomeExUp.text = item.inPlayerName
                    binding.tvHomeExDown.text =item.outPlayerName
                    binding.tvAwayExUp.text = item.inPlayerName
                    binding.tvAwayExDown.text = item.outPlayerName

                }
                else->{
                    binding.vHomeShot.visibleOrGone(false)
                    binding.vHomeExchange.visibleOrGone(false)
                    binding.vAwayCard.visibleOrGone(false)
                    binding.vAwayShot.visibleOrGone(false)
                    binding.vAwayExchange.visibleOrGone(false)
                }
            }
        }
    }
}