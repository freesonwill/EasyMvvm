package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.ui.compare.MatchItemCompare
import arch.cayenne.module.home.ui.viewholder.MatchItemViewHolder

class MatchItemAdapter(private val onMatchItemClickListener: OnMatchItemClickListener? = null) :
    BaseAdapter<MatchWithMarkets, MatchItemViewHolder, ItemMatchCardBinding>(MatchItemCompare()) {
    override fun convertPlus(
        holder: MatchItemViewHolder,
        binding: ItemMatchCardBinding,
        position: Int
    ) {
        binding.layoutOddsTitle.removeAllViews()
        binding.layoutOddsGrid.removeAllViews()
        holder.init(getItem(position))
        binding.layoutLiveEntry.setOnClickListener {
            // 直播入口
            onMatchItemClickListener?.onLiveEntryClick(getItem(holder.adapterPosition))
        }
        binding.ivFavorite.setOnClickListener {
            // 收藏
            onMatchItemClickListener?.onFavoriteClick(getItem(holder.adapterPosition))
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemMatchCardBinding {
        return ItemMatchCardBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemMatchCardBinding,
        viewType: Int
    ): MatchItemViewHolder {
        return MatchItemViewHolder(binding)
    }

    interface OnMatchItemClickListener {
        fun onLiveEntryClick(item: MatchWithMarkets)
        fun onFavoriteClick(item: MatchWithMarkets)
    }
}