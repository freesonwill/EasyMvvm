package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import galaxy.client.proto.Sloth

class TablesCompare : DiffUtil.ItemCallback<Sloth.Table>() {

    override fun areItemsTheSame(oldItem: Sloth.Table, newItem: Sloth.Table): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Sloth.Table, newItem: Sloth.Table): Boolean {
        return oldItem == newItem
    }
}