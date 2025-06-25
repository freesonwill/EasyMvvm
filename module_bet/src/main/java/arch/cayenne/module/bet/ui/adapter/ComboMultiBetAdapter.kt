package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.databinding.ItemComboMultiBetBinding
import arch.cayenne.module.bet.ui.compare.ComboRateCompare
import arch.cayenne.module.bet.ui.viewholder.ComboMultiBetViewHolder

class ComboMultiBetAdapter(
    private val onComboMultiBetClickListener: OnComboMultiBetClickListener
): BaseAdapter<ComboMultiBetBean, ComboMultiBetViewHolder, ItemComboMultiBetBinding>(
    ComboRateCompare()
) {

    override fun convertPlus(holder: ComboMultiBetViewHolder, binding: ItemComboMultiBetBinding, position: Int) {
        val item = getItem(holder.adapterPosition)
        holder.bind(item)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemComboMultiBetBinding {
        return ItemComboMultiBetBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemComboMultiBetBinding, viewType: Int): ComboMultiBetViewHolder {
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
        fun onEditMoneyClick(serialValue: Int, locationX: Int, locationY: Int)
        fun getMoneySymbol(): String
    }
}