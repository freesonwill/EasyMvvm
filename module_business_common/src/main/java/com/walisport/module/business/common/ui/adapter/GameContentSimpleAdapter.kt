package com.walisport.module.business.common.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ThumbHashUtils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.walisport.module.business.common.data.GameContentData
import com.walisport.module.business.common.databinding.ItemGameContentSimpleBinding


class GameContentSimpleAdapter(private val onItemClick: (GameContentData) -> Unit) :
    BaseAdapter<GameContentData, GameContentSimpleViewHolder, ItemGameContentSimpleBinding>(
        GameContentDiff()
    ) {
    override fun convertPlus(
        holder: GameContentSimpleViewHolder,
        binding: ItemGameContentSimpleBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameContentSimpleBinding {
        val binding = ItemGameContentSimpleBinding.inflate(inflater, parent, false)
//        binding.ivGameCover.setCornerRadius(8f)
        return binding
    }

    override fun createViewHolder(
        binding: ItemGameContentSimpleBinding,
        viewType: Int
    ): GameContentSimpleViewHolder {

        return GameContentSimpleViewHolder(onItemClick, binding)
    }

}

class GameContentSimpleViewHolder(
    private val onItemClick: (GameContentData) -> Unit,
    val item: ItemGameContentSimpleBinding
) : BaseViewHolder(item) {
    fun bind(data: GameContentData) {
        val thumbBitmap = ThumbHashUtils.getBitmapFromThumbHash(data.avatar.thumbhash)  //返回 Bitmap?
        thumbBitmap?.let { bitmap ->
            val placeholderDrawable = BitmapDrawable(item.root.context.resources, bitmap)
            Glide.with(item.root)
                .load(data.avatar.url.trim())
                .placeholder(placeholderDrawable)
                .transition(DrawableTransitionOptions.withCrossFade()) // 淡入动画
                .into(item.ivGameCover)

            item.ivGameCover.setCornerRadius(8f)
        }

        // TODO 暫時串接遊戲詳情
        item.root.clickNoRepeat {
            onItemClick.invoke(data)
        }
    }
}


