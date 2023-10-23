package com.xcjh.app.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseDifferAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.DiffEntity

class DiffUtilAdapter: BaseDifferAdapter<DiffEntity, QuickViewHolder>(object : DiffUtil.ItemCallback<DiffEntity>(){
    /**
     * 判断是否是同一个item
     * moveDuration
     */
    override fun areItemsTheSame(oldItem: DiffEntity, newItem: DiffEntity): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * 当是同一个item时，再判断内容是否发生改变
     * update
     *
     */
    override fun areContentsTheSame(oldItem: DiffEntity, newItem: DiffEntity): Boolean {
        return oldItem.title == newItem.title && oldItem.content == newItem.content && oldItem.date == newItem.date
    }

    /**
     *
     * 可选实现
     * 如果需要精确修改某一个view中的内容，请实现此方法。
     * 如果不实现此方法，或者返回null，将会直接刷新整个item。
     */
    override fun getChangePayload(oldItem: DiffEntity, newItem: DiffEntity): Any? {
        return newItem
    }
}) {
    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_differ, parent)
    }

    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: DiffEntity?
    ) {
        holder.setText(R.id.tvId, item!!.id.toString())
            .setText(R.id.tvTitle, item.title)
    }
}