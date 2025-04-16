package arch.cayenne.module.bet.ui.viewholder

import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding
import arch.cayenne.module.bet.util.ViewHelper

class BetSheetViewHolder(private val mBinding: ItemBetSheetBinding): BaseViewHolder(mBinding) {

    fun init(bean: BetBean) {
        ViewHelper.bindBetSheet(bean, mBinding)
    }
}