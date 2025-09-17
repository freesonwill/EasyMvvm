package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.databinding.ItemQuickAmountBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

open class DefaultQuickAmountViewHolder(protected val mBinding: ItemQuickAmountBinding): BaseViewHolder(mBinding) {

    open fun initView() {
    }
}