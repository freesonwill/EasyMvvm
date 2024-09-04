package com.cn.game.sdk2.ui.viewholder

import android.animation.ObjectAnimator
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.utils.IconUtils
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.gameAboutModel

class ChipsViewHolder(private val mBinding: ItemAnnotationListBinding) : BaseViewHolder(mBinding) {

    fun init(item: SelectAnnotationBean) {
        val id = getIconId(item)
        if (id != 0) {
            mBinding.ivShowBg.setImageResource(id)
        }
        if (item.select) {
            if (mBinding.ivShowBg.translationY == 0f) {
                showUpAnim()
            }
        } else {
            mBinding.ivShowBg.translationY = 0f
        }
    }

    private fun getIconId(item: SelectAnnotationBean): Int {
        return if ((gameAboutModel.tempBalance.value ?: 0) < item.money) {
            IconUtils.getIcon("game_sdk_icon_shortage_" + item.moneyPinyin)
        } else {
            if (item.select) {
                IconUtils.getIcon("game_sdk_icon_select_" + item.moneyPinyin)
            } else {
                IconUtils.getIcon("game_sdk_icon_no_" + item.moneyPinyin)
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