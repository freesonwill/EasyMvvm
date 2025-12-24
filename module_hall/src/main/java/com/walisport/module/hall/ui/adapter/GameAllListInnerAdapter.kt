package com.walisport.module.hall.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ThumbHashUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.databinding.ItemGameAllListInnerBinding

//TODO 先暫時用GameContentData，等接api再說
class GameAllListInnerAdapter(private val onItemClickListener: ((Long)->Unit)?) : BaseAdapter<GameContentData, GameListInnerViewHolder, ItemGameAllListInnerBinding>(GameContentDiff()) {
    override fun convertPlus(
        holder: GameListInnerViewHolder,
        binding: ItemGameAllListInnerBinding,
        position: Int
    ) {
        holder.bind(getItem(position),position)
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
class GameListInnerViewHolder(val item: ItemGameAllListInnerBinding,private val onItemClickListener: ((Long)->Unit)?): BaseViewHolder(item) {
    fun bind(data: GameContentData,position: Int) {
        if (position==0){
            LogUtils.e("GameListInnerViewHolder----------${position}")
            item.rvRoot.layoutParams = item.rvRoot.layoutParams.apply {
                width =124.dp2px
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            item.rvRoot.setPadding(11.dp2px,item.rvRoot.paddingTop,7.dp2px,
                item.rvRoot.paddingBottom            )
        }else{
            item.rvRoot.layoutParams = item.rvRoot.layoutParams.apply {
                width =112.dp2px
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            item.rvRoot.setPadding(0.dp2px,item.rvRoot.paddingTop,7.dp2px,
                item.rvRoot.paddingBottom            )
        }
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
            onItemClickListener?.invoke(data.id)
        }
    }
}
