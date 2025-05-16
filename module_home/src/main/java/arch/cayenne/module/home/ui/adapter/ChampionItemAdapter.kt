package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemChampionCardBinding
import arch.cayenne.module.home.ui.adapter.compare.ChampionItemCompare

class ChampionItemAdapter(private val onChampionItemClickListener: OnChampionItemClickListener? = null) :
    BaseAdapter<MarketWithSelections, ChampionItemViewHolder, ItemChampionCardBinding>(ChampionItemCompare()) {
    private val holders = mutableListOf<ChampionItemViewHolder>()
    override fun convertPlus(
        holder: ChampionItemViewHolder,
        binding: ItemChampionCardBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.rvOddsGrid.removeAllViews()
        holder.init(item)
        holders.add(holder)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemChampionCardBinding {
        return ItemChampionCardBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemChampionCardBinding,
        viewType: Int
    ): ChampionItemViewHolder {
        return ChampionItemViewHolder(binding, onChampionItemClickListener)
    }

    override fun onBindViewHolder(
        holder: ChampionItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(holder.adapterPosition)
        if (payloads.isNotEmpty()) {
            holder.bindPayload(item, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}

interface OnChampionItemClickListener {
    fun onOddsCellClick(selection: SelectionBeanLite)
}