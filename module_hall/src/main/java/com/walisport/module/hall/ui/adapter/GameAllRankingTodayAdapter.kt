package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllRankingTodayData
import com.walisport.module.hall.databinding.ItemAllRankingTodayBinding
import java.text.DecimalFormat

class GameAllRankingListTodayAdapter : BaseAdapter<GameAllRankingTodayData, GameAllRankingTodayViewHolder, ItemAllRankingTodayBinding>(GameAllRankingTodayCompare()) {
    override fun convertPlus(
        holder: GameAllRankingTodayViewHolder,
        binding: ItemAllRankingTodayBinding,
        position: Int
    ) {
        holder.bind(getItem(position), position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAllRankingTodayBinding {
        return ItemAllRankingTodayBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemAllRankingTodayBinding,
        viewType: Int
    ): GameAllRankingTodayViewHolder {
        return GameAllRankingTodayViewHolder(binding)
    }
}

class GameAllRankingTodayViewHolder(val item: ItemAllRankingTodayBinding) : BaseViewHolder(item) {
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    fun bind(data: GameAllRankingTodayData, position: Int) {
        with(item) {
            if (position % 2 != 0) {
                item.clRoot.setBackgroundColor(Color.TRANSPARENT)
            } else {
                item.clRoot.setBackgroundResource(R.drawable.shape_game_all_rank_list_bg)
            }

            ivRank.background = null
            tvRank.text = ""
            when(data.rank) {
                1 -> {
                    ivRank.setBackgroundResource(R.drawable.ic_rank_no_1)
                }
                2 -> {
                    ivRank.setBackgroundResource(R.drawable.ic_rank_no_2)
                }
                3 -> {
                    ivRank.setBackgroundResource(R.drawable.ic_rank_no_3)
                }
                else -> {
                    tvRank.text = "${data.rank}"
                }
            }
            tvPlayerName.text = data.playerName
            val plainFormat = DecimalFormat("#.########")
            tvBetting.text = "${data.symbol}${plainFormat.format(data.betting)}"
            tvBonus.text = "${data.symbol}${plainFormat.format(data.bonus)}"
//            ivGame.setImageResource(data.gameIcon)
//            tvGameName.text = data.gameName
//            tvMultiple.text = "${data.multiple}x"
//            if (data.multiple >= 100f) {
//                setTextViewGradient(tvMultiple)
//            } else {
//                tvMultiple.paint.shader = null // 關鍵：清除複用帶來的舊 Shader
//                tvMultiple.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
//            }
//            ivCurrency.setBackgroundResource(arch.cayenne.lib.common.R.drawable.ic_usdt)
//            tvResult.text = "${data.symbol}${data.result}"
//            if (data.result > 0) {
//                tvResult.setTextColor(arch.cayenne.lib.common.R.color.color_00E301.getColor())
//            } else {
//                tvMultiple.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
//            }
        }
    }
}

class GameAllRankingTodayCompare : DiffUtil.ItemCallback<GameAllRankingTodayData>() {
    override fun areItemsTheSame(
        oldItem: GameAllRankingTodayData,
        newItem: GameAllRankingTodayData
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: GameAllRankingTodayData,
        newItem: GameAllRankingTodayData
    ): Boolean  = oldItem == newItem

}