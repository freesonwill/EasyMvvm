package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.home.databinding.ItemOddsColumnBinding
import arch.cayenne.module.home.ui.compare.OddsDiffCallback
import arch.cayenne.module.home.ui.viewholder.OddsColumnViewHolder

class OddsColumnAdapter(
    private val onOddsClick: (SelectionBean, Boolean) -> Unit
) : BaseAdapter<List<SelectionBean>, OddsColumnViewHolder, ItemOddsColumnBinding>(OddsDiffCallback()) {
    private var selectedSelectionId: Long? = null

    fun updateSelectedSelectionId(id: Long?) {
        selectedSelectionId = id
        notifyDataSetChanged()
    }
    override fun convertPlus(
        holder: OddsColumnViewHolder,
        binding: ItemOddsColumnBinding,
        position: Int
    ) {
        holder.bind(getItem(position), selectedSelectionId)
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
        if (payloads.isNotEmpty()) {
            holder.bindPayload(getItem(position), payloads, selectedSelectionId)
        } else {
            holder.bind(getItem(position), selectedSelectionId)
        }
    }
}
