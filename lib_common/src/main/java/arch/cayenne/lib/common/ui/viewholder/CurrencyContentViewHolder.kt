package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencyContentBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class CurrencyContentViewHolder(val mBinding: ItemCurrencyContentBinding) : BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyContentData?) {
        if (item == null) return
        with(mBinding) {
            tvCurrencyName.text = item.currency
            tvCurrencyAmount.text = item.amount
            ivIcon.setBackgroundResource(item.icon)
            mBinding.root.clickNoRepeat {

            }
        }
    }
}