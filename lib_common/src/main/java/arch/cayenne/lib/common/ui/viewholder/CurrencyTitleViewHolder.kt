package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencyTitleBinding

class CurrencyTitleViewHolder(val mBinding: ItemCurrencyTitleBinding) : BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyTitleData?) {
        if (item == null) return
        with(mBinding) {
            tvTitle.text = item.title
        }
    }
}