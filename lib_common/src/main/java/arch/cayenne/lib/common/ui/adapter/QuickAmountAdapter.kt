package arch.cayenne.lib.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.QuickAmountEnum
import arch.cayenne.lib.common.data.constants.QuickAmountKeyboardEnum
import arch.cayenne.lib.common.databinding.ItemQuickAmountBinding
import arch.cayenne.lib.common.ui.viewholder.ComboQuickAmountViewHolder
import arch.cayenne.lib.common.ui.viewholder.DefaultQuickAmountViewHolder
import arch.cayenne.lib.skin.res.SkinnableResourceManager

class QuickAmountAdapter(
    private val type: QuickAmountKeyboardEnum = QuickAmountKeyboardEnum.SINGLE,
    private val onItemClick: (Long) -> Unit
): BaseAdapter<QuickAmountEnum, DefaultQuickAmountViewHolder, ItemQuickAmountBinding>(
    QuickAmountCompare()
) {

    override fun convertPlus(
        holder: DefaultQuickAmountViewHolder,
        binding: ItemQuickAmountBinding,
        position: Int
    ) {
        holder.initView()
        val item = getItem(position)
        val context = binding.root.context
        binding.tvTitle.text = item.value.toString()
        binding.root.setOnClickListener {
            onItemClick(item.getAmount())
        }
        binding.root.foreground = when (position) {
            0 -> SkinnableResourceManager.getDrawable(context, R.drawable.bg_click_mask_top_6dp)
            itemCount - 1 -> SkinnableResourceManager.getDrawable(context, R.drawable.bg_click_mask_bottom_6dp)
            else -> SkinnableResourceManager.getDrawable(context, R.drawable.bg_click_mask)
        }
        binding.line.isVisible = position < itemCount - 1
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
    ): DefaultQuickAmountViewHolder {
        return if (type == QuickAmountKeyboardEnum.COMBO) {
            ComboQuickAmountViewHolder(binding)
        } else {
            DefaultQuickAmountViewHolder(binding)
        }
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