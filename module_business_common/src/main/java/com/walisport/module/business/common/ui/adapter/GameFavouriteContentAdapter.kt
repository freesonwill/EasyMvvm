package com.walisport.module.business.common.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ThumbHashUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.walisport.module.business.common.data.GameContentData
import com.walisport.module.business.common.data.HotColdType
import com.walisport.module.business.common.databinding.ItemGameContentBinding
import com.walisport.module.business.common.databinding.ItemGameFavouriteContentBinding

class GameFavouriteContentAdapter(private val onItemClick: (GameContentData) -> Unit) :
    BaseAdapter<GameContentData, GameFavouriteContentViewHolder, ItemGameFavouriteContentBinding>(
        GameContentDiff()
    ) {
    override fun convertPlus(
        holder: GameFavouriteContentViewHolder,
        binding: ItemGameFavouriteContentBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameFavouriteContentBinding {
        return ItemGameFavouriteContentBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameFavouriteContentBinding,
        viewType: Int
    ): GameFavouriteContentViewHolder {
        return GameFavouriteContentViewHolder(onItemClick, binding)
    }

}

class GameFavouriteContentViewHolder(
    private val onItemClick: (GameContentData) -> Unit,
    val item: ItemGameFavouriteContentBinding
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
        }
        item.tvCount.text = data.online.toString()

        item.llCount.visibility = ViewGroup.VISIBLE
        //TODO 判斷

        item.llCount.apply {
            val params = this.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 6.dp2px
        }


        // TODO 暫時串接遊戲詳情
        item.root.clickNoRepeat {
            onItemClick.invoke(data)
        }
    }
}


