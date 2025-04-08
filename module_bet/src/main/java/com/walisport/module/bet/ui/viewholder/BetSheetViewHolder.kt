package com.walisport.module.bet.ui.viewholder

import com.walisport.lib.base.viewholder.BaseViewHolder
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.databinding.ItemBetSheetBinding

class BetSheetViewHolder(private val mBinding: ItemBetSheetBinding): BaseViewHolder(mBinding) {

    fun init(bean: BetBean) {
        val odds = "@${bean.odds}"
        mBinding.tvOdds.text = odds

        mBinding.tvMatchName.text = bean.matchName
        mBinding.tvLeagueName.text = bean.leagueName
    }
}