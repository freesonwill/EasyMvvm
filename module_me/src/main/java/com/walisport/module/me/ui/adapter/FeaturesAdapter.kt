package com.walisport.module.me.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import com.walisport.module.me.data.model.FeaturesBean
import com.walisport.module.me.databinding.ItemFeaturesBinding
import com.walisport.module.me.ui.adapter.compare.FeatureBeanCompare
import com.walisport.module.me.ui.viewholder.FeaturesViewHolder

class FeaturesAdapter(): BaseAdapter<FeaturesBean, FeaturesViewHolder, ItemFeaturesBinding>(
    FeatureBeanCompare()
) {
    override fun convertPlus(holder: FeaturesViewHolder, binding: ItemFeaturesBinding, position: Int) {
        val bean = getItem(position)
        holder.init(bean)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemFeaturesBinding {
        return ItemFeaturesBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemFeaturesBinding, viewType: Int): FeaturesViewHolder {
        return FeaturesViewHolder(binding)
    }

}