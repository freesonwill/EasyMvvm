package arch.cayenne.module.home.ui.viewholder

import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.utils.ViewHelper

class MatchItemViewHolder(private val mBinding: ItemMatchCardBinding) : BaseViewHolder(mBinding) {

    fun init(data: MatchWithMarkets) {
        ViewHelper.bindMatchItem(data, mBinding)
    }
}