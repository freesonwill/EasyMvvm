package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import com.walisport.module.live.compare.SoftDataCompare
import com.walisport.module.live.data.constants.EmojiTypeEnum
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.data.model.SoftData
import com.walisport.module.live.databinding.ItemSoftAdapterBinding

/**
 * @author: wenxi
 * @date: 4/6/25 10:02
 * @description:
 */
class SoftAdapter :
    BaseAdapter<SoftData, SoftAdapter.SoftViewHolder, ItemSoftAdapterBinding>(compare = SoftDataCompare()) {
    private var itemListener: RecyclerItemListener<EmojiData>? = null

    fun setItemListener(itemListener: RecyclerItemListener<EmojiData>) {
        this.itemListener = itemListener
    }

    inner class SoftViewHolder(val nBinding: ItemSoftAdapterBinding) : BaseViewHolder(nBinding) {
        fun iniAdapter() {
            val adapter = LiveEmojiAdapter()
            adapter.setItemListener(itemListener)
            val manager = GridLayoutManager(nBinding.softRecycler.context,8)
            nBinding.softRecycler.layoutManager = manager
            nBinding.softRecycler.adapter = adapter
        }
    }

    override fun convertPlus(
        holder: SoftViewHolder,
        binding: ItemSoftAdapterBinding,
        position: Int
    ) {
        val data = getItem(position)
        binding.softRecycler.apply {
            isInvisible = false
            val nManager = layoutManager?.let { it as GridLayoutManager }
            nManager?.spanCount = if (data.emojiType == EmojiTypeEnum.NORMAL) 8 else 4
            val nAdapter = adapter?.let { it as LiveEmojiAdapter }
            nAdapter?.submitList(data.emojis)
            adapter = nAdapter
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSoftAdapterBinding {
        return ItemSoftAdapterBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemSoftAdapterBinding, viewType: Int): SoftViewHolder {
        val holder = SoftViewHolder(binding)
        holder.iniAdapter()
        return holder
    }
}