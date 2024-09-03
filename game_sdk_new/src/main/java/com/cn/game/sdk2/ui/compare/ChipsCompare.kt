package com.cn.game.sdk2.ui.compare

import androidx.recyclerview.widget.DiffUtil
import com.cn.game.sdk2.data.bean.SelectAnnotationBean

class ChipsCompare : DiffUtil.ItemCallback<SelectAnnotationBean>() {
    override fun areItemsTheSame(
        oldItem: SelectAnnotationBean,
        newItem: SelectAnnotationBean
    ): Boolean {
        return oldItem.money == newItem.money || oldItem.select == newItem.select
    }

    override fun areContentsTheSame(
        oldItem: SelectAnnotationBean,
        newItem: SelectAnnotationBean
    ): Boolean {
        return oldItem == newItem
    }
}