package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.databinding.ItemComboMultiBet2Binding
import arch.cayenne.module.bet.ui.compare.ComboRateCompare
import arch.cayenne.module.bet.ui.custom.BetMoneyKeyboard
import arch.cayenne.module.bet.ui.viewholder.ComboMultiBetViewHolder

class ComboMultiBetAdapter(
    val onComboMultiBetClickListener: OnComboMultiBetClickListener
): BaseAdapter<ComboMultiBetBean, ComboMultiBetViewHolder, ItemComboMultiBet2Binding>(
    ComboRateCompare()
) {

    override fun convertPlus(holder: ComboMultiBetViewHolder, binding: ItemComboMultiBet2Binding, position: Int) {
        val item = getItem(holder.adapterPosition)
        holder.bind(item)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemComboMultiBet2Binding {
        return ItemComboMultiBet2Binding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemComboMultiBet2Binding, viewType: Int): ComboMultiBetViewHolder {
        return ComboMultiBetViewHolder(binding, onComboMultiBetClickListener)
    }

    override fun onBindViewHolder(
        holder: ComboMultiBetViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(holder.adapterPosition)
            holder.updateMoney(item) // 局部更新
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    interface OnComboMultiBetClickListener {
        //投注金额点击
        @Deprecated("use onEditMoneyClick2 instead")
        fun onEditMoneyClick(serialValue: Int, locationX: Int, locationY: Int) {}
        fun onEditMoneyClick2(serialValue: Int, editText: EditText, tvMoney: TextView, addViewAction:(keyboard:BetMoneyKeyboard)->Unit){ }
        //货币符号
        fun getMoneySymbol(): String
        //组合详情
        fun onCombinationDetailClick(serialValue: Int) {}
    }
}