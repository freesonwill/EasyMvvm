package com.walisport.module.bet.ui.viewholder

import android.annotation.SuppressLint
import com.walisport.lib.base.viewholder.BaseViewHolder
import com.walisport.module.bet.R
import com.walisport.module.bet.data.ComboRateBean
import com.walisport.module.bet.databinding.ItemComboRateBinding
import com.walisport.module.bet.ui.adapter.ComboRateAdapter

class ComboRateViewHolder(private val mBinding: ItemComboRateBinding, private val onComboRateClickListener: ComboRateAdapter.OnComboRateClickListener): BaseViewHolder(mBinding) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(size: Int, item: ComboRateBean) {
        val combo = getString(R.string.title_combo_bet_rate).format(size, item.combo)
        val title = "$combo @${item.rate}"
        mBinding.tvTitleCombo.text = title

        val money = if (item.money.isNotEmpty()) {
            "\$ ${item.money}"
        } else {
            ""
        }
        mBinding.etMoney.setText(money)

        mBinding.etMoney.isFocusable = false
        mBinding.etMoney.setOnClickListener {
            val location = IntArray(2)
            mBinding.etMoney.getLocationOnScreen(location)
            val x = location.first() + mBinding.etMoney.width / 2
            val y = location.last()
            onComboRateClickListener.onEditRateClick(item.combo, x, y, item.money)
        }
    }
}