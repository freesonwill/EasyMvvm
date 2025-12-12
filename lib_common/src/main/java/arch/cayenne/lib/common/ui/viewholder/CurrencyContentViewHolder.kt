package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencyContentBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class CurrencyContentViewHolder(
    val mBinding: ItemCurrencyContentBinding,
    val listener: ((BaseCurrencyData.CurrencyContentData2) -> Unit)?) : BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyContentData2?) {
        if (item == null) return
        with(mBinding) {
            tvCurrencyName.text = item.currencyName
            tvCurrencyAmount.text = "${item.unit}${item.amount}"
            mBinding.root.clickNoRepeat {
                listener?.invoke(item)
            }
        }
    }
}