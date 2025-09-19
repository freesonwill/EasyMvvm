package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.common.databinding.ItemQuickAmountBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class ComboQuickAmountViewHolder(mBinding: ItemQuickAmountBinding): DefaultQuickAmountViewHolder(mBinding) {

    override fun initView() {
        mBinding.tvTitle.setPadding(0, 9.dp2px, 0, 8.dp2px)
    }
}