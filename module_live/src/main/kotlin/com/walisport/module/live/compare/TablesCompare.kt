package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.TableBean

class TablesCompare : DiffUtil.ItemCallback<TableBean>() {

    override fun areItemsTheSame(oldItem: TableBean, newItem: TableBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TableBean, newItem: TableBean): Boolean {
        return oldItem == newItem
    }
}