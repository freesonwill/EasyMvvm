package com.cn.game.sdk2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.compare.GameHallItemCompare
import com.cn.game.sdk2.ui.viewholder.BaseViewHolder

class GameHallItemAdapter : BaseAdapter<GameHallItem, BaseViewHolder, ItemGamehallPageItemBinding>(
    GameHallItemCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemGamehallPageItemBinding,
        item: GameHallItem
    ) {
        binding.tvName.text = item.name
        binding.tvOnline.text = item.online.toString()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGamehallPageItemBinding {
        return ItemGamehallPageItemBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemGamehallPageItemBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

}