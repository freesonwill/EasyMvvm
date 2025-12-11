package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencySettingBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class CurrencySettingViewHolder(val mBinding: ItemCurrencySettingBinding): BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyContentData2) {
        mBinding.tvCurrencyName.text = item.currencyName
        mBinding.root.clickNoRepeat {

        }
    }
}