package arch.cayenne.module.bet.ui.viewholder

import android.annotation.SuppressLint
import android.text.StaticLayout
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.databinding.ItemComboMultiBet2Binding
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter

class ComboMultiBetViewHolder(private val mBinding: ItemComboMultiBet2Binding, private val onComboMultiBetClickListener: ComboMultiBetAdapter.OnComboMultiBetClickListener): BaseViewHolder(mBinding) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: ComboMultiBetBean) {
        val combo = getString(R.string.title_combo_bet_odds).format(item.comboK, item.comboV)
        val title = "$combo @${item.odds.getOdds()}"
        mBinding.tvTitleCombo.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.tvTitleCombo.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val isTooLong = isTextTooLong(mBinding.tvTitleCombo, title)
                mBinding.tvTitleCombo.text = when {
                    item.isSuperCombo -> getString(R.string.title_combo_bet_super)
                    isTooLong ->  "$combo\n@${item.sumOdds.getOdds()}"
                    else ->  title
                }
                mBinding.tvTitleCombo.maxLines = if (isTooLong) 2 else 1
            }
        })
        val multi = "${item.count}x"
        mBinding.tvMulti.text = multi

        updateMoney(item)

        mBinding.etMoney.isFocusable = false
        mBinding.etMoney.setOnClickListener {
            onComboMultiBetClickListener.onEditMoneyClick2(item.serialValue, mBinding.etMoney,mBinding.tvMoney,addViewAction = { keyboard->
                val lp = ConstraintLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                    topToBottom = mBinding.tvPrincipal.id
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                }
                mBinding.root.addView(keyboard,lp)
            })
        }

        mBinding.tvTitleCombo.clickNoRepeat {
            onComboMultiBetClickListener.onCombinationDetailClick(item.serialValue)
        }
    }

    fun updateMoney(item: ComboMultiBetBean) {
        val moneySymbol = onComboMultiBetClickListener.getMoneySymbol()
        if (item.inputMoney > 0) {
            val money = item.inputMoney.getMoney()
            mBinding.etMoney.setText(money)
        } else {
            mBinding.etMoney.setText("")
        }
        val moneyHint = getString(R.string.et_money_hint).format(item.minAmount.getMoney(), item.maxAmount.getMoney())
        mBinding.etMoney.hint = moneyHint
        mBinding.tvMoney.text = moneySymbol
        /*val amountMoney = "$moneySymbol${item.amount.getFormalMoney()}"
        mBinding.tvMoney.text = amountMoney
        val maxMoney = "$moneySymbol${item.maxWinMoney.getFormalMoney()}"
        mBinding.tvMaxMoney.text = maxMoney*/
    }

    private fun isTextTooLong(textView: TextView, text: String): Boolean {
        val maxWidthPx = textView.measuredWidth - textView.paddingLeft - textView.paddingRight
        val paint = textView.paint
        //创建一个“离屏文本布局器” StaticLayout，计算存在多少行
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidthPx)
            .build()
        return staticLayout.lineCount > 1
    }
}