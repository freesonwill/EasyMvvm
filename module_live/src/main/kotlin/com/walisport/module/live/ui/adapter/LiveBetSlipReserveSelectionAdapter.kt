package com.walisport.module.live.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.skin.widget.SportView
import com.walisport.module.live.R
import com.walisport.module.live.compare.LiveBetSlipReserveSelectionCompare
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.utils.LiveDateUtil
import galaxy.common.proto.Common

class LiveBetSlipReserveSelectionAdapter(val showGradient: Boolean) :
    BaseAdapter<Common.ReserveOrderSelection, LiveBetSlipReserveSelectionAdapter.LiveBetSlipReserveSelectionViewHolder, ItemLiveBetSlipReserveBinding>(
        LiveBetSlipReserveSelectionCompare()
    ) {
    val gradient = showGradient

    inner class LiveBetSlipReserveSelectionViewHolder(binding: ItemLiveBetSlipReserveBinding) :
        BaseViewHolder(binding) {
        val nBinding = binding

        fun initView(position: Int, count: Int) {
            configView(nBinding.line, nBinding.groupGradient, position, count)
        }

        private fun configView(line: SportView, group: Group, position: Int, count: Int) {
            line.isVisible = position != count - 1
            group.isVisible = position == 2 && gradient
        }

        @SuppressLint("SetTextI18n")
         fun updateData(item: Common.ReserveOrderSelection) {
            val match = item.matchBasic
            //TODO icon没有数据
            // Glide.with(root.context).load(match.tournamentIcon).into(betUnsettledIvBall)

            with(nBinding){
                betReserveTvRace.text = match.matchName
                betReserveTvIntroduce.text = item.selectionName
                betReserveTvAodds.text = ContextCompat.getString(binding.root.context,R.string.live_bet_except_odds)+"  @"+item.odds
//                TODO 滚球不清楚
                betReserveTvStatus.text = "滚球"
                betReserveTvScore.text = ContextCompat.getString(binding.root.context,R.string.live_bet_full_handicap)+"  "+match.liveInfo.score
                betReserveTvStart.text = LiveDateUtil.getMDHm(match.startTime)
            }
        }


    }

    override fun convertPlus(
        holder: LiveBetSlipReserveSelectionViewHolder,
        binding: ItemLiveBetSlipReserveBinding,
        position: Int
    ) {
        holder.initView(position, itemCount)
        holder.updateData(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemLiveBetSlipReserveBinding {
        return ItemLiveBetSlipReserveBinding.inflate(inflater, parent, false)

    }

    override fun createViewHolder(
        binding: ItemLiveBetSlipReserveBinding, viewType: Int
    ): LiveBetSlipReserveSelectionViewHolder {
        val holder = LiveBetSlipReserveSelectionViewHolder(binding)
        return holder
    }


}