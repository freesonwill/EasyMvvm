package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemOddsColumnBinding
import arch.cayenne.module.home.ui.adapter.compare.OddsDiffCallback

class OddsColumnAdapter(
    private val onMatchItemClickListener: OnMatchItemClickListener?
) : BaseAdapter<Pair<MarketBeanLite, List<SelectionBeanLite>>, OddsColumnViewHolder, ItemOddsColumnBinding>(
    OddsDiffCallback()
) {
    var onOddsClick: ((SelectionBeanLite, Boolean) -> Unit)? = null
    override fun convertPlus(
        holder: OddsColumnViewHolder,
        binding: ItemOddsColumnBinding,
        position: Int
    ) {
        val (market, selections) = getItem(position)
        holder.bind(market, selections)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemOddsColumnBinding {
        return ItemOddsColumnBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemOddsColumnBinding,
        viewType: Int
    ): OddsColumnViewHolder {
        return OddsColumnViewHolder(binding, onMatchItemClickListener)
    }

    override fun onBindViewHolder(
        holder: OddsColumnViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        val (market, selections) = getItem(position)
        if (payloads.isNotEmpty()) {
            holder.bindPayload(market, selections, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}
