package arch.cayenne.module.bet.ui.viewholder

import android.annotation.SuppressLint
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.databinding.ItemComboMultiBetBinding
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter

class ComboMultiBetViewHolder(private val mBinding: ItemComboMultiBetBinding, private val onComboMultiBetClickListener: ComboMultiBetAdapter.OnComboMultiBetClickListener): BaseViewHolder(mBinding) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: ComboMultiBetBean) {
        val combo = getString(R.string.title_combo_bet_odds).format(onComboMultiBetClickListener.getSize(), item.combo)
        val title = "$combo @${item.sumOdds.getOdds()}"
        mBinding.tvTitleCombo.text = title

        updateMoney(item)

        mBinding.etMoney.isFocusable = false
        mBinding.etMoney.setOnClickListener {
            val location = IntArray(2)
            mBinding.etMoney.getLocationOnScreen(location)
            val x = location.first() + mBinding.etMoney.width / 2
            val y = location.last()
            onComboMultiBetClickListener.onEditMoneyClick(item.combo, x, y)
        }
    }

    fun updateMoney(item: ComboMultiBetBean) {
        val money = if (item.inputMoney > 0) {
            "\$ ${item.inputMoney.getMoney()}"
        } else {
            "\$ ${getString(R.string.et_money_hint).format(item.minAmount.getMoney(), item.maxAmount.getMoney())}"
        }
        mBinding.etMoney.setText(money)
        val amountMoney = "\$${item.amount.getMoney()}"
        mBinding.tvMoney.text = amountMoney
        val maxMoney = "\$${item.maxWinMoney.getMoney()}"
        mBinding.tvMaxMoney.text = maxMoney
    }
}