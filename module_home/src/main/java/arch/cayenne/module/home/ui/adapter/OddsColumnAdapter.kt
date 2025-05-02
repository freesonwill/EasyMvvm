package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemOddsColumnBinding
import arch.cayenne.module.home.ui.compare.OddsDiffCallback
import arch.cayenne.module.home.ui.viewholder.OddsColumnViewHolder

class OddsColumnAdapter(
    private val onOddsClick: (SelectionBeanLite, Boolean) -> Unit
) : BaseAdapter<Pair<MarketBeanLite, List<SelectionBeanLite>>, OddsColumnViewHolder, ItemOddsColumnBinding>(
    OddsDiffCallback()
) {

//    fun updateSelectedSelectionId(id: Long?) {
//        selectedSelectionId = id
//        notifyDataSetChanged()
//    }
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
        return OddsColumnViewHolder(binding, onOddsClick)
    }

    override fun onBindViewHolder(
        holder: OddsColumnViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
//        if (payloads.isNotEmpty()) {
//            holder.bindPayload(getItem(position), payloads)
//        } else {
//            holder.bind(getItem(position))
//        }
        val (market, selections) = getItem(position)
        if (payloads.isNotEmpty()) {
            holder.bindPayload(market, selections, payloads)
        } else {
            holder.bind(market, selections)
        }
    }
}
