package com.cn.game.sdk2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.ItemDrawHistoryBinding
import com.cn.game.sdk2.ui.compare.RoundInfoCompare
import com.cn.game.sdk2.ui.viewholder.BaseViewHolder
import com.cn.game.sdk2.utils.ext.BizExt.isLeopard
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.websocket.bean.RoundInfoBean

class DrawHistoryAdapter : BaseAdapter<RoundInfoBean, BaseViewHolder, ItemDrawHistoryBinding>(
    RoundInfoCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemDrawHistoryBinding,
        item: RoundInfoBean
    ) {
        item.performs.forEachIndexed { index, perform ->
            val child = binding.llShowDice.getChildAt(index) as ImageView
            val id = holder.resources.getIdentifier(
                "game_sdk_icon_dice_" + perform.toPinyin(),
                "mipmap",
                holder.itemView.context.packageName
            )
            child.setImageResource(id)
        }
        binding.txtBetNum.text = item.sum.toString()
        if (item.isLeopard) {
            binding.txtBetSize.text = holder.getString(R.string.g_home_txt_leopard)
            binding.txtBetOdd.text = holder.getString(R.string.g_home_txt_leopard)
            binding.txtBetSize.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.game_sdk_shape_3_01933b)
            binding.txtBetOdd.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.game_sdk_shape_3_01933b)

        } else {
            binding.txtBetSize.background =
                ContextCompat.getDrawable(holder.itemView.context, if (item.isBig) R.drawable.game_sdk_shape_3_b83030 else R.drawable.game_sdk_shape_3_006ce4)
            binding.txtBetOdd.background =
                ContextCompat.getDrawable(holder.itemView.context, if (item.isDouble) R.drawable.game_sdk_shape_3_b83030 else R.drawable.game_sdk_shape_3_006ce4)
            binding.txtBetSize.text =
                if (item.isBig) {
                    holder.getString(R.string.g_home_txt_big)
                } else {
                    holder.getString(R.string.g_home_txt_small)
                }
            binding.txtBetOdd.text = if (item.isDouble) {
                holder.getString(R.string.g_home_txt_double)
            } else {
                holder.getString(R.string.g_home_txt_single)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDrawHistoryBinding {
        return ItemDrawHistoryBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemDrawHistoryBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}