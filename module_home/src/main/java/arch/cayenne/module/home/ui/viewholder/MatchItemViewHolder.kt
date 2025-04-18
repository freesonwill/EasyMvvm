package arch.cayenne.module.home.ui.viewholder

import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.module.home.data.Match
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.utils.ViewHelper

class MatchItemViewHolder(private val mBinding: ItemMatchCardBinding) : BaseViewHolder(mBinding) {

    fun init(data: Match) {
        ViewHelper.bindMatchItem(data, mBinding)
    }
}