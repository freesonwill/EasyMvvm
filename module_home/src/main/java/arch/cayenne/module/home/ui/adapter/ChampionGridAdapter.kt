package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemChampionOddsCellBinding
import arch.cayenne.module.home.ui.adapter.compare.OddDiffCompare

class ChampionGridAdapter(
    private val onOddsClick: (SelectionBeanLite, Boolean) -> Unit
) : BaseAdapter<SelectionBeanLite, ChampionOddsCellViewHolder, ItemChampionOddsCellBinding>(OddDiffCompare()){
    override fun convertPlus(
        holder: ChampionOddsCellViewHolder,
        binding: ItemChampionOddsCellBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemChampionOddsCellBinding {
        return ItemChampionOddsCellBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemChampionOddsCellBinding, viewType: Int): ChampionOddsCellViewHolder {
        return ChampionOddsCellViewHolder(binding, onOddsClick)
    }

    override fun onBindViewHolder(
        holder: ChampionOddsCellViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(holder.adapterPosition)
        if (payloads.isNotEmpty()) {
            holder.bindPayload(item, payloads)
        } else {
            holder.bind(item)
        }
    }
}