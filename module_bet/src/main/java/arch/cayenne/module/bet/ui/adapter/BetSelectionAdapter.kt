package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding
import arch.cayenne.module.bet.ui.compare.BetSelectionBeanCompare
import arch.cayenne.module.bet.ui.viewholder.BetSelectionViewHolder

class BetSelectionAdapter(private val onBetSelectionClickListener: OnBetSelectionClickListener? = null): BaseAdapter<BetSelectionBean, BetSelectionViewHolder, ItemBetSheetBinding>(
    BetSelectionBeanCompare()
) {
    override fun convertPlus(
        holder: BetSelectionViewHolder,
        binding: ItemBetSheetBinding,
        position: Int
    ) {
        holder.init(getItem(position))
        binding.ivDelete.setOnClickListener {
            onBetSelectionClickListener?.onDeleteClick(getItem(holder.adapterPosition))
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemBetSheetBinding {
        return ItemBetSheetBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemBetSheetBinding, viewType: Int): BetSelectionViewHolder {
        return BetSelectionViewHolder(binding)
    }

    interface OnBetSelectionClickListener {
        fun onDeleteClick(item: BetSelectionBean)
    }
}