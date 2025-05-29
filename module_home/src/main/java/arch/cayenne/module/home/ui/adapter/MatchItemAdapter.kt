package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.ui.adapter.compare.MatchItemCompare

class MatchItemAdapter(private val onMatchItemClickListener: OnMatchItemClickListener? = null) :
    BaseAdapter<MatchWithMarkets, MatchItemViewHolder, ItemMatchCardBinding>(MatchItemCompare()) {
    override fun convertPlus(
        holder: MatchItemViewHolder,
        binding: ItemMatchCardBinding,
        position: Int
    ) {
        val item = getItem(position)
//        binding.layoutOddsTitle.removeAllViews()
//        binding.rvOddsGrid.removeAllViews()
        holder.init(item)

        binding.clLeftInfoEntry.setOnClickListener {
            onMatchItemClickListener?.onLiveEntryClick(getItem(holder.adapterPosition))
        }
        binding.ivFavorite.setOnClickListener {
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
        return MatchItemViewHolder(binding, onMatchItemClickListener)
    }

    override fun onBindViewHolder(
        holder: MatchItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(holder.adapterPosition)
            holder.bindPayload(item, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}



interface OnMatchItemClickListener {
    fun onLiveEntryClick(item: MatchWithMarkets)
    fun onFavoriteClick(item: MatchWithMarkets)
    fun onOddsCellClick(item: MatchWithMarkets, selection: SelectionBeanLite)
}