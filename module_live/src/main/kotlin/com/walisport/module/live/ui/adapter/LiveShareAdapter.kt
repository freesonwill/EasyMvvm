package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import com.walisport.module.live.compare.LiveShareCompare
import com.walisport.module.live.data.model.LiveShareBean
import com.walisport.module.live.databinding.ItemLiveShareItemBinding
import com.walisport.module.live.ui.viewholder.LiveShareViewHolder

class LiveShareAdapter(): BaseAdapter<LiveShareBean, LiveShareViewHolder, ItemLiveShareItemBinding>(
    LiveShareCompare()
) {
    override fun convertPlus(holder: LiveShareViewHolder, binding: ItemLiveShareItemBinding, position: Int) {
        val bean = getItem(position)
        holder.init(bean)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemLiveShareItemBinding {
        return ItemLiveShareItemBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemLiveShareItemBinding, viewType: Int): LiveShareViewHolder {
        return LiveShareViewHolder(binding)
    }

}