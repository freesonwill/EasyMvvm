package com.cn.game.sdk2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.ui.compare.ChipsCompare
import com.cn.game.sdk2.ui.viewholder.ChipsViewHolder

class ChipsAdapter : BaseAdapter<SelectAnnotationBean, ChipsViewHolder, ItemAnnotationListBinding>(
    ChipsCompare()
) {

    override fun convertPlus(
        holder: ChipsViewHolder,
        binding: ItemAnnotationListBinding,
        item: SelectAnnotationBean
    ) {
        holder.init(item)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAnnotationListBinding {
        return ItemAnnotationListBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemAnnotationListBinding,
        viewType: Int
    ): ChipsViewHolder {
        return ChipsViewHolder(binding)
    }
}