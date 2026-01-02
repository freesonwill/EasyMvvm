package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencySettingBinding

class CurrencySettingViewHolder(val mBinding: ItemCurrencySettingBinding) :
    BaseViewHolder(mBinding) {

    fun bind(item: BaseCurrencyData.CurrencyContentData) {
        val str = item.unit + " " + item.currencyName
        mBinding.tvCurrencyName.text = str
        mBinding.root.isSelected = item.isSelected
    }
}