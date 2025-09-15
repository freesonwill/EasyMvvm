package arch.cayenne.lib.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.QuickAmountEnum
import arch.cayenne.lib.common.databinding.ItemQuickAmountBinding

class QuickAmountAdapter(
    private val onItemClick: (Long) -> Unit
): BaseAdapter<QuickAmountEnum, BaseViewHolder, ItemQuickAmountBinding>(
    QuickAmountCompare()
) {

    companion object {
        private const val MAX_DISPLAY_COUNT = 5
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemQuickAmountBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvTitle.text = item.value.toString()
        binding.root.setOnClickListener {
            onItemClick(item.getAmount())
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemQuickAmountBinding {
        return ItemQuickAmountBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemQuickAmountBinding,
        viewType: Int
    ): BaseViewHolder {
        return getBaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        // 回傳完整列表數量和最大顯示數量的較小值。
        // 這確保了如果你的列表少於五個，它只會顯示現有的項目。
        return minOf(currentList.size, MAX_DISPLAY_COUNT)
    }
}

class QuickAmountCompare: DiffUtil.ItemCallback<QuickAmountEnum>() {
    override fun areItemsTheSame(
        oldItem: QuickAmountEnum,
        newItem: QuickAmountEnum
    ): Boolean {
        return oldItem.value == newItem.value
    }

    override fun areContentsTheSame(
        oldItem: QuickAmountEnum,
        newItem: QuickAmountEnum
    ): Boolean {
        return oldItem == newItem
    }
}