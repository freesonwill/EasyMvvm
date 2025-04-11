package com.walisport.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.walisport.lib.base.adapter.BaseAdapter
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.databinding.ItemBetSheetBinding
import com.walisport.module.bet.ui.compare.BetBeanCompare
import com.walisport.module.bet.ui.viewholder.BetSheetViewHolder

class BetSheetAdapter(private val onBetSheetClickListener: OnBetSheetClickListener): BaseAdapter<BetBean, BetSheetViewHolder, ItemBetSheetBinding>(
    BetBeanCompare()
) {
    override fun convertPlus(
        holder: BetSheetViewHolder,
        binding: ItemBetSheetBinding,
        position: Int
    ) {
        holder.init(getItem(position))
        binding.ivDelete.setOnClickListener {
            onBetSheetClickListener.onDeleteClick(getItem(holder.adapterPosition))
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemBetSheetBinding {
        return ItemBetSheetBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemBetSheetBinding, viewType: Int): BetSheetViewHolder {
        return BetSheetViewHolder(binding)
    }

    interface OnBetSheetClickListener {
        fun onDeleteClick(item: BetBean)
    }
}