package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.home.data.SportBannerData
import arch.cayenne.module.home.databinding.ItemSportBannerBinding

class SportBannerAdapter :
    BaseAdapter<SportBannerData, SportBannerViewHolder, ItemSportBannerBinding>(SportBannerCompare()) {
    var originItemCount = 0

    override fun convertPlus(
        holder: SportBannerViewHolder,
        binding: ItemSportBannerBinding,
        position: Int
    ) {
        if (originItemCount == 0) return
        val realPosition = position % originItemCount
        holder.bind(getItem(realPosition))
    }

    override fun submitList(list: List<SportBannerData?>?) {
        super.submitList(list)
        originItemCount = list?.size ?: 0
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSportBannerBinding {
        return ItemSportBannerBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemSportBannerBinding,
        viewType: Int
    ): SportBannerViewHolder {
        return SportBannerViewHolder(binding)
    }
}

class SportBannerViewHolder(val mBinding: ItemSportBannerBinding) : BaseViewHolder(mBinding) {
    fun bind(item: SportBannerData) {
        mBinding.ivBanner.setImageResource(item.res)
    }
}

class SportBannerCompare : DiffUtil.ItemCallback<SportBannerData>() {
    override fun areItemsTheSame(
        oldItem: SportBannerData,
        newItem: SportBannerData
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: SportBannerData,
        newItem: SportBannerData
    ): Boolean = oldItem == newItem
}

