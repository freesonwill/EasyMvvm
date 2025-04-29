package arch.cayenne.module.bet.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding
import arch.cayenne.module.bet.util.ViewHelper

class BetSelectionViewHolder(private val mBinding: ItemBetSheetBinding): BaseViewHolder(mBinding) {

    fun init(bean: BetSelectionBean) {
        ViewHelper.bindBetSheet(bean, mBinding)
    }
}