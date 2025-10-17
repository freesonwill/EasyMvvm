package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.hall.data.GameAllBannerData
import com.walisport.module.hall.databinding.ItemGameAllBannerBinding

class GameAllBannerAdapter : BaseAdapter<GameAllBannerData, GameAllBannerViewHolder, ItemGameAllBannerBinding>(GameAllBannerCompare()) {
    override fun convertPlus(
        holder: GameAllBannerViewHolder,
        binding: ItemGameAllBannerBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameAllBannerBinding {
        return ItemGameAllBannerBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameAllBannerBinding,
        viewType: Int
    ): GameAllBannerViewHolder {
        return GameAllBannerViewHolder(binding)
    }
}

class GameAllBannerViewHolder(val mBinding: ItemGameAllBannerBinding): BaseViewHolder(mBinding) {
    fun bind(item: GameAllBannerData) {
        mBinding.ivBanner.setImageResource(item.res)
    }
}

class GameAllBannerCompare : DiffUtil.ItemCallback<GameAllBannerData>() {
    override fun areItemsTheSame(
        oldItem: GameAllBannerData,
        newItem: GameAllBannerData
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: GameAllBannerData,
        newItem: GameAllBannerData
    ): Boolean  = oldItem == newItem

}