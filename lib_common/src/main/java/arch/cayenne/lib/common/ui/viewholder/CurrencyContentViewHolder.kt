package arch.cayenne.lib.common.ui.viewholder

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
    val listener: ((BaseCurrencyData.CurrencyContentData2) -> Unit)?) : BaseViewHolder(mBinding) {
    fun bind(item: BaseCurrencyData.CurrencyContentData2?, isLastItem: Boolean) {
        if (item == null) return
        with(mBinding) {
            if (isLastItem) {
                clRoot.setBackgroundResource(R.drawable.selector_currency_item_last_background)
            } else {
                clRoot.setBackgroundResource(R.drawable.selector_currency_item_background)
            }
            clRoot.isSelected = item.isSelected
            tvCurrencyName.text = item.currencyName
            tvCurrencyAmount.text = "${item.unit}${item.amountStr}"
            Glide.with(root.context)
                .load(item.icon)
                .placeholder(R.drawable.ic_wali_demo)
                .error(R.drawable.ic_wali_demo)
                .into(ivIcon)
            var topMargin = 0
            if (item.exchangeAmount == "") {
                tvCurrencyExchange.visibility = View.GONE
                topMargin = 5
                tvCurrencyAmount.setPadding(0,0,0, 0)
            } else {
                tvCurrencyExchange.visibility = View.VISIBLE
                tvCurrencyExchange.text = item.exchangeAmount
                topMargin = 0
                tvCurrencyAmount.setPadding(0,0,0, 5)
            }
            val params = tvCurrencyAmount.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = topMargin
            tvCurrencyAmount.layoutParams = params

            mBinding.root.clickNoRepeat {
                listener?.invoke(item)
            }

        }
    }
}