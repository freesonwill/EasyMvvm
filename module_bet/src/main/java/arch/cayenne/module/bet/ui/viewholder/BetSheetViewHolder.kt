package arch.cayenne.module.bet.ui.viewholder

import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.IntExt.getOdds
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding

class BetSheetViewHolder(private val mBinding: ItemBetSheetBinding): BaseViewHolder(mBinding) {

    fun init(bean: BetBean) {
        val odds = "@${bean.odds.getOdds()}"
        mBinding.tvOdds.text = odds

        mBinding.tvBetTeamName.text = bean.betTeamName
        mBinding.tvMatchName.text = bean.matchName
        mBinding.tvLeagueName.text = bean.leagueName
    }
}