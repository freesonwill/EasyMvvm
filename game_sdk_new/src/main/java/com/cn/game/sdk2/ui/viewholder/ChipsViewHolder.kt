package com.cn.game.sdk2.ui.viewholder

import android.animation.ObjectAnimator
import com.cn.game.sdk2.data.enums.ChipBean
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.gameAboutModel

class ChipsViewHolder(private val mBinding: ItemAnnotationListBinding) : BaseViewHolder(mBinding) {

    fun init(item: ChipBean) {
        val id = getIconId(item)
        mBinding.ivShowBg.setImageResource(id)

        if (item.isSelected) {
            if (mBinding.ivShowBg.translationY == 0f) {
                showUpAnim()
            }
        } else {
            mBinding.ivShowBg.translationY = 0f
        }
    }

    private fun getIconId(item: ChipBean): Int {
        return if ((gameAboutModel.tempBalance.value ?: 0) < item.chip.money) {
            item.chip.disableRes
        } else {
            if (item.isSelected) {
                item.chip.selectedRes
            } else {
                item.chip.enableRes
            }
        }
    }

    private fun showUpAnim() {
        val anim =
            ObjectAnimator.ofFloat(mBinding.ivShowBg, "translationY", -itemView.context.dp2px(5).toFloat())
        anim.duration = 100L
        anim.start()
    }
}