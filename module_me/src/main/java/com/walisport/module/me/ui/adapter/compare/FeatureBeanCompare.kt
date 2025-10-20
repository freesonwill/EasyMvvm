package com.walisport.module.me.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.me.data.model.FeaturesBean

class FeatureBeanCompare : DiffUtil.ItemCallback<FeaturesBean>() {
    override fun areItemsTheSame(oldItem: FeaturesBean, newItem: FeaturesBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeaturesBean, newItem: FeaturesBean): Boolean {
        return oldItem == newItem
    }
}