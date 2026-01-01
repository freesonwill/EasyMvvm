package com.walisport.module.search.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ThumbHashUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.walisport.module.search.R
import com.walisport.module.search.databinding.ItemSearchGameCardBinding
import com.walisport.module.search.ui.model.HotColdType
import com.walisport.module.search.ui.model.SearchGameContentData

/**
 * 搜索結果頁的遊戲卡片 Adapter。
 * UI 風格與大廳遊戲卡片一致，但完全定義在 search 模組內，避免模組耦合。
 * 資料結構使用 search 模組的 GameContentData，與 hall 模組對齊欄位命名。
 */
class SearchGameCardAdapter(
    private val onItemClick: (SearchGameContentData) -> Unit
) : ListAdapter<SearchGameContentData, SearchGameCardAdapter.GameCardViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCardViewHolder {
        val binding = ItemSearchGameCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GameCardViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: GameCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GameCardViewHolder(
        private val binding: ItemSearchGameCardBinding,
        private val onItemClick: (SearchGameContentData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchGameContentData) {
            with(binding) {
                // 1. ThumbHash 佔位圖（與 GameContentViewHolder 一致）
                if (item.avatar.thumbhash.isNotEmpty()) {
                    val thumbBitmap = ThumbHashUtils.getBitmapFromThumbHash(item.avatar.thumbhash)
                    val placeholderDrawable = BitmapDrawable(root.context.resources, thumbBitmap)
                    Glide.with(root)
                        .load(item.avatar.url.trim())
                        .placeholder(placeholderDrawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivGameCover)
                } else {
                    // mock 沒有 thumbhash 時，退回預設佔位圖
                    Glide.with(root)
                        .load(item.avatar.url.trim())
                        .placeholder(R.drawable.ic_search_result_placeholder)
                        .into(ivGameCover)
                }

                // 2. 在線人數
                tvCount.text = item.online.toString()

                // 3. 熱／冷標記與返獎率（與 hall 模組邏輯一致）
                if (item.hotOrCold != HotColdType.NONE) {
                    llBack.visibility = ViewGroup.VISIBLE
                    if (item.hotOrCold == HotColdType.HOT) {
                        ivHot.setImageResource(R.drawable.ic_search_game_hot)
                    } else {
                        ivHot.setImageResource(R.drawable.ic_search_game_cold)
                    }
                    tvBack.text = String.format("%.1f%%", item.reward)
                    (llCount.layoutParams as ConstraintLayout.LayoutParams).apply {
                        topMargin = 5.dp2px
                        llCount.layoutParams = this
                    }
                } else {
                    llBack.visibility = ViewGroup.GONE
                    (llCount.layoutParams as ConstraintLayout.LayoutParams).apply {
                        topMargin = 6.dp2px
                        llCount.layoutParams = this
                    }
                }

                // 4. 點擊事件
                root.clickNoRepeat { onItemClick(item) }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<SearchGameContentData>() {
        override fun areItemsTheSame(oldItem: SearchGameContentData, newItem: SearchGameContentData): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SearchGameContentData, newItem: SearchGameContentData): Boolean =
            oldItem == newItem
    }
}



