package com.walisport.module.hall.ui.adapter

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
import com.bumptech.glide.request.RequestOptions
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.databinding.ItemGameContentBinding

class GameContentAdapter(private val onItemClick: (GameContentData) -> Unit) : BaseAdapter<GameContentData, GameContentViewHolder, ItemGameContentBinding>(GameContentDiff()) {
    override fun convertPlus(
        holder: GameContentViewHolder,
        binding: ItemGameContentBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameContentBinding {
        return ItemGameContentBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameContentBinding,
        viewType: Int
    ): GameContentViewHolder {
        return GameContentViewHolder(onItemClick,binding)
    }

}

class GameContentViewHolder(private val onItemClick: (GameContentData) -> Unit,val item: ItemGameContentBinding): BaseViewHolder(item) {
    fun bind(data: GameContentData) {
        val thumbBitmap = ThumbHashUtils.getBitmapFromThumbHash(data.avatar.thumbhash)  //返回 Bitmap?
        thumbBitmap?.let { bitmap ->
            val placeholderDrawable = BitmapDrawable(item.root.context.resources, bitmap)
            Glide.with(item.root)
                .load(data.avatar.url)
                .placeholder(placeholderDrawable)
                .transition(DrawableTransitionOptions.withCrossFade()) // 淡入动画
                .into(item.ivGameCover)
        }
        item.tvCount.text = data.online.toString()
        //TODO 判斷
        if (data.hotOrCold != HotColdType.NONE) {
            item.llBack.visibility = ViewGroup.VISIBLE
            if (data.hotOrCold == HotColdType.HOT) {
                item.ivHot.setImageResource(R.drawable.ic_game_hot)
            } else {
                item.ivHot.setImageResource(R.drawable.ic_game_cold)
            }
            item.tvBack.text = "${data.reward}%"
            item.llCount.apply {
                val params = this.layoutParams as ConstraintLayout.LayoutParams
                params.topMargin = 5.dp2px
            }
        } else {
            item.llBack.visibility = ViewGroup.GONE
            item.llCount.apply {
                val params = this.layoutParams as ConstraintLayout.LayoutParams
                params.topMargin = 6.dp2px
            }
        }


        // TODO 暫時串接遊戲詳情
        item.root.clickNoRepeat {
            onItemClick.invoke(data)
        }
    }
}

class GameContentDiff : DiffUtil.ItemCallback<GameContentData>() {
    override fun areItemsTheSame(
        oldItem: GameContentData,
        newItem: GameContentData
    ): Boolean = oldItem.id == newItem.id


    override fun areContentsTheSame(
        oldItem: GameContentData,
        newItem: GameContentData
    ): Boolean  = oldItem.id == newItem.id

}

