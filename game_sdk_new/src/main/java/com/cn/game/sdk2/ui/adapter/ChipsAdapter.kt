package com.cn.game.sdk2.ui.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.ui.compare.ChipsCompare
import com.cn.game.sdk2.ui.viewholder.BaseViewHolder
import com.cn.game.sdk2.utils.IconUtils
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.gameAboutModel

class ChipsAdapter : BaseAdapter<SelectAnnotationBean, BaseViewHolder, ItemAnnotationListBinding>(
    ChipsCompare()
) {

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemAnnotationListBinding,
        item: SelectAnnotationBean
    ) {
        val id = if ((gameAboutModel.tempBalance.value ?: 0) < item.money) {
            IconUtils.getIcon("game_sdk_icon_shortage_" + item.moneyPinyin)
        } else {
            if (item.select) {
                IconUtils.getIcon("game_sdk_icon_select_" + item.moneyPinyin)
            } else {
                IconUtils.getIcon("game_sdk_icon_no_" + item.moneyPinyin)
            }
        }
        if (id != 0) {
            binding.ivShowBg.setImageResource(id)
        }
        //Log.d(TAG, "onBind-->${layoutPosition},bean:${bean}")
        if (item.select) {
            if (binding.ivShowBg.translationY == 0f) {
                val anim =
                    ObjectAnimator.ofFloat(binding.ivShowBg, "translationY", -holder.itemView.context.dp2px(5).toFloat())
                anim.duration = 100L
                anim.start()
            }
        } else {
            binding.ivShowBg.translationY = 0f
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAnnotationListBinding {
        return ItemAnnotationListBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemAnnotationListBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}