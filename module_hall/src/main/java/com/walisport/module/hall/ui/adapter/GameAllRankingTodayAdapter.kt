package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllRankingToday
import com.walisport.module.hall.databinding.ItemAllRankingDashBinding
import com.walisport.module.hall.databinding.ItemAllRankingTodayBinding
import java.text.DecimalFormat

class GameAllRankingListTodayAdapter :
    BaseAdapter<GameAllRankingToday , BaseViewHolder , ViewBinding>(GameAllRankingTodayCompare()) {
    override fun convertPlus(
        holder: BaseViewHolder ,
        binding: ViewBinding ,
        position: Int
    ) {
        val item = getItem(position)
        when (item) {
            is GameAllRankingToday.GameAllRankingTodayData -> {
                (holder as? GameAllRankingTodayViewHolder)?.bind(item , position)
            }

            else -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (getItem(position)) {
            is GameAllRankingToday.GameAllRankingTodayData -> {
                return GameAllRankingListTodayEnum.DATA.ordinal
            }

            is GameAllRankingToday.GameAllRankingDashData -> {
                return GameAllRankingListTodayEnum.DASH.ordinal
            }

        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater ,
        parent: ViewGroup ,
        viewType: Int
    ): ViewBinding {
        when (viewType) {
            GameAllRankingListTodayEnum.DATA.ordinal -> {
                return ItemAllRankingTodayBinding.inflate(inflater , parent , false)
            }

            else -> {
                return ItemAllRankingDashBinding.inflate(inflater , parent , false)
            }
        }

    }

    override fun createViewHolder(
        binding: ViewBinding ,
        viewType: Int
    ): BaseViewHolder {
        when (viewType) {
            GameAllRankingListTodayEnum.DATA.ordinal -> {
                return GameAllRankingTodayViewHolder(binding as ItemAllRankingTodayBinding)
            }

            else -> {
                return GameAllRankingDashViewHolder(binding as ItemAllRankingDashBinding)
            }
        }
    }
}

class GameAllRankingTodayViewHolder(val item: ItemAllRankingTodayBinding) : BaseViewHolder(item) {
    @SuppressLint("ClickableViewAccessibility" , "SetTextI18n")
    fun bind(data: GameAllRankingToday.GameAllRankingTodayData , position: Int) {
        with(item) {
            if (data.rank % 2 == 0) {
                item.clRoot.setBackgroundColor(Color.TRANSPARENT)
            } else {
                item.clRoot.setBackgroundResource(R.drawable.shape_game_all_rank_list_bg)
            }

            ivRank.background = null
            tvRank.text = ""
            when (data.rank) {
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
                    // 设置排名文本，如果排名大于 5000，则显示 "5000+"，否则显示具体排名
                    tvRank.text = if (data.rank > 5000) "5000+" else "${data.rank}"                }
            }
            tvPlayerName.text = data.playerName
            val plainFormat = DecimalFormat("#.########")
            tvBetting.text = "${data.symbol}${plainFormat.format(data.betting / 100)}"
            tvBonus.text = "${data.symbol}${plainFormat.format(data.bonus / 100)}"

            if (data.myself) {
                tvRank.setTextColor(arch.cayenne.lib.common.R.color.color_00E0E5.getColor())
                tvPlayerName.setTextColor(arch.cayenne.lib.common.R.color.color_00E0E5.getColor())
                tvBetting.setTextColor(arch.cayenne.lib.common.R.color.color_00E0E5.getColor())
                tvBonus.setTextColor(arch.cayenne.lib.common.R.color.color_00E0E5.getColor())
            } else {
                tvRank.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
                tvPlayerName.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
                tvBetting.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
                tvBonus.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
            }

        }
    }
}

class GameAllRankingDashViewHolder(val item: ItemAllRankingDashBinding) : BaseViewHolder(item) {

}

class GameAllRankingTodayCompare : DiffUtil.ItemCallback<GameAllRankingToday>() {
    override fun areItemsTheSame(
        oldItem: GameAllRankingToday ,
        newItem: GameAllRankingToday
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: GameAllRankingToday ,
        newItem: GameAllRankingToday
    ): Boolean = oldItem == newItem
}

enum class GameAllRankingListTodayEnum {
    DATA , DASH
}