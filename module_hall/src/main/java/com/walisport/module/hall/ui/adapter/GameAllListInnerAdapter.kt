package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.databinding.ItemGameAllListInnerBinding

//TODO 先暫時用GameContentData，等接api再說
class GameAllListInnerAdapter(private val onItemClickListener: (()->Unit)?) : BaseAdapter<GameContentData, GameListInnerViewHolder, ItemGameAllListInnerBinding>(GameContentDiff()) {
    override fun convertPlus(
        holder: GameListInnerViewHolder,
        binding: ItemGameAllListInnerBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameAllListInnerBinding {
        return ItemGameAllListInnerBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameAllListInnerBinding,
        viewType: Int
    ): GameListInnerViewHolder {
        return GameListInnerViewHolder(binding,onItemClickListener)
    }
}

class GameListInnerViewHolder(val item: ItemGameAllListInnerBinding,private val onItemClickListener: (()->Unit)?): BaseViewHolder(item) {
    fun bind(data: GameContentData) {
        item.ivGameCover.setImageResource(data.cover)

        // TODO 暫時串接遊戲詳情
        item.root.clickNoRepeat {
            onItemClickListener?.invoke()
        }
    }
}
