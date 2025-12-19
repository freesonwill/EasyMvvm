package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import com.bumptech.glide.Glide
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.databinding.ItemAllRankingListBinding

class GameAllRankingListAdapter : BaseAdapter<GameAllRankingListData, GameAllRankingListViewHolder, ItemAllRankingListBinding>(GameAllRankingListCompare()) {
    override fun convertPlus(
        holder: GameAllRankingListViewHolder,
        binding: ItemAllRankingListBinding,
        position: Int
    ) {
        holder.bind(getItem(position), position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAllRankingListBinding {
        return ItemAllRankingListBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemAllRankingListBinding,
        viewType: Int
    ): GameAllRankingListViewHolder {
        return GameAllRankingListViewHolder(binding)
    }
}

class GameAllRankingListViewHolder(val item: ItemAllRankingListBinding) : BaseViewHolder(item) {
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    fun bind(data: GameAllRankingListData, position: Int) {
        with(item) {
            if (position % 2 != 0) {
                item.clRoot.setBackgroundColor(Color.TRANSPARENT)
            } else {
                item.clRoot.setBackgroundResource(R.drawable.shape_game_all_rank_list_bg)
            }
            Glide.with(ivGame.context).load(data.gameIconUrl).into(ivGame)

            tvGameName.text = data.gameName
            //倍数数据：由数字和“x”的倍数符号组成。
            //需完整展示，需显示到小数点后两位，100倍以上的倍数采用火热渐变色。
            //后台下发的赔率是整形，需要除以100
            tvMultiple.text = "${data.multiple.getOdds()}x"
            if (data.multiple >= 10000f) {
                setTextViewGradient(tvMultiple)
            } else {
                tvMultiple.paint.shader = null // 關鍵：清除複用帶來的舊 Shader
                tvMultiple.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
            }
            ivCurrency.setBackgroundResource(arch.cayenne.lib.common.R.drawable.ic_usdt)
            //负号 + 法币符号 + 金额 + 币种
            if (data.result < 0) {
                tvResult.text = "-${data.symbol}${-data.result}"
            } else {
                tvResult.text = "${data.symbol}${data.result}"
            }

            if (data.result > 0) {
                tvResult.setTextColor(arch.cayenne.lib.common.R.color.color_00E301.getColor())
            } else {
                tvResult.setTextColor(arch.cayenne.lib.common.R.color.color_C0C0C0.getColor())
            }
        }
    }

    private fun setTextViewGradient(textView: TextView) {
        textView.post {
            val textWidth = textView.measuredWidth.toFloat()
            val textHeight = textView.measuredHeight.toFloat()

            if (textWidth == 0f || textHeight == 0f) return@post

            val linearGradient = LinearGradient(
                0f, 0f,
                0f, textHeight,
                intArrayOf(
                    "#FFA14C".toColorInt(),
                    "#FF6B47".toColorInt(),
                    "#FE4F44".toColorInt()
                ),
                null,
                Shader.TileMode.CLAMP
            )

            textView.paint.shader = linearGradient
            textView.invalidate()
        }
    }
}

class GameAllRankingListCompare : DiffUtil.ItemCallback<GameAllRankingListData>() {
    override fun areItemsTheSame(
        oldItem: GameAllRankingListData,
        newItem: GameAllRankingListData
    ): Boolean = false

    override fun areContentsTheSame(
        oldItem: GameAllRankingListData,
        newItem: GameAllRankingListData
    ): Boolean  = false

}