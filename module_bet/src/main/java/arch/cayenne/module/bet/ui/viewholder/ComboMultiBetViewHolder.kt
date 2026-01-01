package arch.cayenne.module.bet.ui.viewholder

import android.text.Editable
import android.text.StaticLayout
import android.text.TextWatcher
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.databinding.ItemComboMultiBet2Binding
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter
import java.util.Locale

class ComboMultiBetViewHolder(private val mBinding: ItemComboMultiBet2Binding, private val onComboMultiBetClickListener: ComboMultiBetAdapter.OnComboMultiBetClickListener): BaseViewHolder(mBinding) {

    fun bind(item: ComboMultiBetBean) {
        val combo = R.string.title_combo_bet_tittle.getString(item.comboK, item.comboV)
        mBinding.tvTitleCombo.text = when {
            item.isSuperCombo -> R.string.title_combo_bet_super.getString()
            else ->  combo
        }
        val multi = "${item.count}x"
        mBinding.tvMulti.text = multi

        updateMoney(item)

        mBinding.etMoney.isFocusable = false
        mBinding.etMoney.setOnClickListener {
            onComboMultiBetClickListener.onEditMoneyClick2(item.serialValue, mBinding.etMoney,mBinding.tvMoney,mBinding.root,addViewAction = { keyboard->
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

        mBinding.etMoney.apply{
            // 1. 移除旧的 watcher
            removeTextChangedListener(getTag(arch.cayenne.lib.common.R.id.tag_text_watcher_key) as? TextWatcher)
            // 2. 创建新的 watcher
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val money = text.toString().toMoney()
                    val product = (money * item.count).getMoney()
                    mBinding.tvPrincipal.text = String.format(Locale.ROOT,"${R.string.title_result_amount.getString()} %s%s",
                        onComboMultiBetClickListener.getMoneySymbol(),
                        product
                    )
                }
            }
            // 3. 把 watcher 存到 EditText 的 tag 上
            setTag(arch.cayenne.lib.common.R.id.tag_text_watcher_key,watcher)
            // 4. 初始化调用一次
            watcher.afterTextChanged(text)
            // 5. 添加监听
            addTextChangedListener(watcher)
        }
    }

    fun updateMoney(item: ComboMultiBetBean) {
        val moneySymbol = onComboMultiBetClickListener.getMoneySymbol()
        mBinding.etMoney.setText(item.inputMoneyStr)
        val moneyHint = R.string.et_money_hint.getString(item.minAmount.getMoney(), item.maxAmount.getMoney())
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