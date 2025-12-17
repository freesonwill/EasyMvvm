package arch.cayenne.lib.common.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencyContentBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide

class CurrencyContentViewHolder(
    val mBinding: ItemCurrencyContentBinding,
    val listener: ((BaseCurrencyData.CurrencyContentData2) -> Unit)?) : BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyContentData2?) {
        if (item == null) return
        with(mBinding) {
            tvCurrencyName.text = item.currencyName
            tvCurrencyAmount.text = "${item.unit}${item.amountStr}"
            Glide.with(root.context)
                .load(item.icon)
                .placeholder(R.drawable.ic_wali_demo)
                .error(R.drawable.ic_wali_demo)
                .into(ivIcon)
            mBinding.root.clickNoRepeat {
                listener?.invoke(item)
            }

        }
    }
}