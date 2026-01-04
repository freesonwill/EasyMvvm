package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencySettingBinding
import arch.cayenne.lib.common.ui.adapter.CurrencySettingAdapter.OnItemClickListener
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class CurrencySettingViewHolder(val mBinding: ItemCurrencySettingBinding) :
    BaseViewHolder(mBinding) {

    fun bind(
        item: BaseCurrencyData.CurrencyContentData,
        listener: OnItemClickListener?
    ) {
        val str = item.unit + " " + item.currencyName
        mBinding.tvCurrencyName.text = str
        mBinding.tvCurrencyName.isSelected = item.fiatSelected
        mBinding.root.isSelected = item.fiatSelected
        mBinding.root.clickNoRepeat {
            listener?.onItemClick(item)
        }
    }
}