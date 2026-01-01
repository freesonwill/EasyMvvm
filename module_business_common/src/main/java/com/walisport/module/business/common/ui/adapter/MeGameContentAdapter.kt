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
import com.walisport.module.business.common.databinding.MeItemGameContentBinding

class MeGameContentAdapter(private val onItemClick: (GameContentData) -> Unit) : BaseAdapter<GameContentData, MeGameContentViewHolder, MeItemGameContentBinding>(MeGameContentDiff()) {
    override fun convertPlus(
        holder: MeGameContentViewHolder,
        binding: MeItemGameContentBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): MeItemGameContentBinding {
        return MeItemGameContentBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: MeItemGameContentBinding,
        viewType: Int
    ): MeGameContentViewHolder {
        return MeGameContentViewHolder(onItemClick,binding)
    }

}

class MeGameContentViewHolder(private val onItemClick: (GameContentData) -> Unit,val item: MeItemGameContentBinding): BaseViewHolder(item) {
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
        if (data.hotOrCold != HotColdType.NONE) {
            item.llBack.visibility = ViewGroup.VISIBLE
            if (data.hotOrCold == HotColdType.HOT) {
                item.ivHot.setImageResource(com.walisport.module.business.common.R.drawable.ic_game_flame)
            } else {
                item.ivHot.setImageResource(com.walisport.module.business.common.R.drawable.ic_game_snow)
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

class MeGameContentDiff : DiffUtil.ItemCallback<GameContentData>() {
    override fun areItemsTheSame(
        oldItem: GameContentData,
        newItem: GameContentData
    ): Boolean = false


    override fun areContentsTheSame(
        oldItem: GameContentData,
        newItem: GameContentData
    ): Boolean  = false

}

