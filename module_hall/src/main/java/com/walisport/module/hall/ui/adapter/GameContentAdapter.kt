package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.databinding.ItemGameContentBinding

class GameContentAdapter : BaseAdapter<GameContentData, GameContentViewHolder, ItemGameContentBinding>(GameContentDiff()) {
    override fun convertPlus(
        holder: GameContentViewHolder,
        binding: ItemGameContentBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameContentBinding {
        return ItemGameContentBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameContentBinding,
        viewType: Int
    ): GameContentViewHolder {
        return GameContentViewHolder(binding)
    }

}

class GameContentViewHolder(val item: ItemGameContentBinding): BaseViewHolder(item) {
    fun bind(data: GameContentData) {
        item.ivGameCover.setImageResource(data.cover)
    }
}

class GameContentDiff : DiffUtil.ItemCallback<GameContentData>() {
    override fun areItemsTheSame(
        oldItem: GameContentData,
        newItem: GameContentData
    ): Boolean = oldItem.cover == newItem.cover


    override fun areContentsTheSame(
        oldItem: GameContentData,
        newItem: GameContentData
    ): Boolean  = oldItem.cover == newItem.cover

}

