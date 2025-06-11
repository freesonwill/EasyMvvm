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

    var isExpanded = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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

    override fun getItemCount(): Int {
        return if (isExpanded) currentList.size else minOf(1, currentList.size)
    }

    fun toggleExpand() {
        isExpanded = !isExpanded
    }

    override fun getItem(position: Int): ComboMultiBetBean {
        return if (isExpanded) {
            currentList[position]
        } else {
            currentList.last()
        }
    }

    override fun submitList(list: List<ComboMultiBetBean>?, commitCallback: Runnable?) {
        val runnable = Runnable {
            commitCallback?.run()
            if (!isExpanded) {
                notifyItemChanged(0)
            }
        }
        super.submitList(list, runnable)
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
        fun onEditMoneyClick(id: Int, locationX: Int, locationY: Int)
        fun getSize(): Int
        fun getMoneySymbol(): String
    }
}