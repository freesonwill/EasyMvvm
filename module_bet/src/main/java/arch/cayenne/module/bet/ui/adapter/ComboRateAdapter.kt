package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.module.bet.data.ComboRateBean
import arch.cayenne.module.bet.databinding.ItemComboRateBinding
import arch.cayenne.module.bet.ui.compare.ComboRateCompare
import arch.cayenne.module.bet.ui.viewholder.ComboRateViewHolder

class ComboRateAdapter(
    private val onComboRateClickListener: OnComboRateClickListener
): BaseAdapter<ComboRateBean, ComboRateViewHolder, ItemComboRateBinding>(
    ComboRateCompare()
) {

    var isExpanded = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convertPlus(holder: ComboRateViewHolder, binding: ItemComboRateBinding, position: Int) {
        val item = getItem(holder.adapterPosition)
        holder.bind(currentList.size, item)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemComboRateBinding {
        return ItemComboRateBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemComboRateBinding, viewType: Int): ComboRateViewHolder {
        return ComboRateViewHolder(binding, onComboRateClickListener)
    }

    override fun getItemCount(): Int {
        return if (isExpanded) currentList.size else minOf(1, currentList.size)
    }

    fun toggleExpand() {
        isExpanded = !isExpanded
    }

    override fun getItem(position: Int): ComboRateBean {
        return if (isExpanded) {
            currentList[position]
        } else {
            currentList.last()
        }
    }

    override fun submitList(list: List<ComboRateBean>?, commitCallback: Runnable?) {
        val runnable = Runnable {
            commitCallback?.run()
            if (!isExpanded) {
                notifyItemChanged(0)
            }
        }
        super.submitList(list, runnable)
    }

    interface OnComboRateClickListener {
        fun onEditRateClick(id: Int, locationX: Int, locationY: Int, rate: String)
    }
}