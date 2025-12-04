package com.walisport.module.hall.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ThumbHashUtils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.databinding.ItemGameAllListInnerBinding

//TODO 先暫時用GameContentData，等接api再說
class GameAllListInnerAdapter(private val onItemClickListener: (()->Unit)?) : BaseAdapter<GameContentData, GameListInnerViewHolder, ItemGameAllListInnerBinding>(GameContentDiff()) {
    override fun convertPlus(
        holder: GameListInnerViewHolder,
        binding: ItemGameAllListInnerBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
        if (position==0){
             binding.rvRoot.setPadding(0,binding.rvRoot.paddingTop,binding.rvRoot.paddingRight,binding.rvRoot.paddingEnd)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameAllListInnerBinding {
        return ItemGameAllListInnerBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameAllListInnerBinding,
        viewType: Int
    ): GameListInnerViewHolder {
        return GameListInnerViewHolder(binding,onItemClickListener)
    }
}

class GameListInnerViewHolder(val item: ItemGameAllListInnerBinding,private val onItemClickListener: (()->Unit)?): BaseViewHolder(item) {
    fun bind(data: GameContentData) {
        val thumbBitmap = ThumbHashUtils.getBitmapFromThumbHash(data.avatar.thumbhash)  // 你之前写好的函数，返回 Bitmap?
        thumbBitmap?.let { bitmap ->
            val placeholderDrawable = BitmapDrawable(item.root.context.resources, bitmap)
            Glide.with(item.root)
                .load(data.avatar.url)
                .placeholder(placeholderDrawable)
                .transition(DrawableTransitionOptions.withCrossFade()) // 淡入动画
                .into(item.ivGameCover)
        }
        item.tvCount.text = data.online.toString()
        // TODO 暫時串接遊戲詳情
        item.root.clickNoRepeat {
            onItemClickListener?.invoke()
        }
    }
}
