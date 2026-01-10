package arch.cayenne.lib.common.ui.viewholder

import android.text.TextUtils
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencyContentBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide

class CurrencyContentViewHolder(
    val mBinding: ItemCurrencyContentBinding,
    val listener: ((BaseCurrencyData.CurrencyContentData) -> Unit)?) : BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyContentData?, isLastItem: Boolean) {
        if (item == null) return
        with(mBinding) {
            if (isLastItem) {
                clRoot.setBackgroundResource(R.drawable.selector_currency_item_last_background)
            } else {
                clRoot.setBackgroundResource(R.drawable.selector_currency_item_background)
            }
            clRoot.isSelected = item.isSelected
            tvCurrencyName.text = item.currencyName
            tvCurrencyExchange.text = item.exchangeAmount
            Glide.with(root.context)
                .load(item.icon)
                .placeholder(R.drawable.ic_wali_demo)
                .error(R.drawable.ic_wali_demo)
                .into(ivIcon)
            if (TextUtils.isEmpty(item.exchangeAmount)) {
                tvCurrencyAmount.text = "${item.unit}${item.amountStr}"
                tvCurrencyExchange.visibility = View.GONE
                tvCurrencyAmount.setPadding(0, -5, 0, 0)
            } else {
                tvCurrencyAmount.text = "${item.amountStr}"
                tvCurrencyExchange.visibility = View.VISIBLE
                if (item.ccy != "USDT") {
                    tvCurrencyAmount.setPadding(0, -5, 0, 0)
                } else {
                    tvCurrencyAmount.setPadding(0, 0, 0, 0)
                }
            }
            mBinding.root.clickNoRepeat {
                listener?.invoke(item)
            }
        }
    }
}