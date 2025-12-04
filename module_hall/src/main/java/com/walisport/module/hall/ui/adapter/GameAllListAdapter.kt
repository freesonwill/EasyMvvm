package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllContentData
import com.walisport.module.hall.databinding.ItemGameAllListBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.hall.ui.adapter.GameAllListViewHolder.OnAllItemClickListener

/**
 * 全部類型的遊戲頭部Adapter，包含左方的廣告位、右方的邀請朋友和每日比賽
 * */

class GameAllListAdapter( private val onItemClickListener: OnAllItemClickListener?) : BaseAdapter<GameAllContentData, GameAllListViewHolder, ItemGameAllListBinding>(GameAllContentDiff()) {
    override fun convertPlus(
        holder: GameAllListViewHolder,
        binding: ItemGameAllListBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameAllListBinding {
        return ItemGameAllListBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameAllListBinding,
        viewType: Int
    ): GameAllListViewHolder {
        return GameAllListViewHolder(binding,onItemClickListener)
    }
}

class GameAllListViewHolder(val item: ItemGameAllListBinding,var onItemClickListener: OnAllItemClickListener? = null): BaseViewHolder(item) {

    fun bind(data: GameAllContentData) {
        item.rvInnerList.layoutManager =
            LinearLayoutManager(item.rvInnerList.context, LinearLayoutManager.HORIZONTAL, false)

        item.rvInnerList.adapter = GameAllListInnerAdapter(onItemClickListener?.run {::onChildItemClick} as (() -> Unit)?).also {
            it.submitList(data.gameList)
        }
        item.tvTitle.text = data.name
        item.root.clickNoRepeat {
            onItemClickListener?.onItemClick()
        }
    }
    interface OnAllItemClickListener {
        fun onItemClick()
        fun onChildItemClick()
    }
}
class GameAllContentDiff : DiffUtil.ItemCallback<GameAllContentData>() {
    override fun areItemsTheSame(
        oldItem: GameAllContentData,
        newItem: GameAllContentData
    ): Boolean = oldItem.id == newItem.id


    override fun areContentsTheSame(
        oldItem: GameAllContentData,
        newItem: GameAllContentData
    ): Boolean  = oldItem.id == newItem.id

}






