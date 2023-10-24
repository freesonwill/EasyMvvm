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
                0 -> {
                    //开始比赛
                    setUI(binding)
                }
                1 -> {
                    //进球
                    setUI(binding,2,item.position)
                    binding.tvHomeShotScore.text = "${item.homeScore} - ${item.awayScore}"
                    binding.tvHomeShotMsg.text = item.playerName
                    binding.tvAwayShotScore.text = "${item.homeScore} - ${item.awayScore}"
                    binding.tvAwayShotMsg.text =  item.playerName
                }
                3, 4 ,15-> {
                    //红黄牌
                    setUI(binding,1,item.position)
                    binding.tvHomeMsg.text = item.playerName
                    binding.ivHomeIcon.setImageResource(if (item.type == 3) R.drawable.yellow_card else R.drawable.red_card)
                    binding.tvAwayMsg.text =item.playerName
                    binding.ivAwayIcon.setImageResource(if (item.type == 3) R.drawable.yellow_card else R.drawable.red_card)
                }
                9 -> {
                    //换人
                    setUI(binding,3,item.position)
                    binding.tvHomeExUp.text = item.inPlayerName
                    binding.tvHomeExDown.text =item.outPlayerName
                    binding.tvAwayExUp.text = item.inPlayerName
                    binding.tvAwayExDown.text = item.outPlayerName
                }
                11->{
                    //中场
                    binding.tvTime.text = "中场"
                    setUI(binding)
                }
                12->{
                    //中场
                    binding.tvTime.text = "结束"
                    setUI(binding)
                }
                else->{
                    setUI(binding)
                }
            }
        }
    }

    private fun setUI(binding: ItemImportEventBinding,type:Int=0,pos:Int=0) {

        binding.vHomeCard.visibleOrGone(if (type==0) false else { type==1&&pos==1 })
        binding.vHomeShot.visibleOrGone(if (type==0) false else { type==2&&pos==1 })
        binding.vHomeExchange.visibleOrGone(if (type==0) false else { type==3&&pos==1 })
        binding.vAwayCard.visibleOrGone(if (type==0) false else { type==1&&pos==2 })
        binding.vAwayShot.visibleOrGone(if (type==0) false else { type==2&&pos==2 })
        binding.vAwayExchange.visibleOrGone(if (type==0) false else { type==3&&pos==2 })
    }
}