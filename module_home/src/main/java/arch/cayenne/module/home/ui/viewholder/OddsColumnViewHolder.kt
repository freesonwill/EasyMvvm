package arch.cayenne.module.home.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.home.databinding.ItemOddsColumnBinding

class OddsColumnViewHolder(
    mBinding: ItemOddsColumnBinding, // odds_row_item.xml 的 binding
    onOddsClick: (SelectionBean, Boolean) -> Unit
) : BaseViewHolder(mBinding) {

    private val oddsCells = listOf(
        OddsCellViewHolder(mBinding.itemOddsCell1, onOddsClick),
        OddsCellViewHolder(mBinding.itemOddsCell2, onOddsClick),
        OddsCellViewHolder(mBinding.itemOddsCell3, onOddsClick)
    )

    fun bind(selections: List<SelectionBean>, selectedId: Long?) {
        oddsCells.forEachIndexed { index, cell ->
            selections.getOrNull(index)?.let {
                cell.bind(it, selectedId)
            } ?: cell.hideView()
        }
    }

    fun bindPayload(selections: List<SelectionBean>, payloads: List<Any>, selectedId: Long?) {
        val changes = payloads.firstOrNull() as? Set<*> ?: return
        oddsCells.forEachIndexed { index, cell ->
            val selection = selections.getOrNull(index)
            if (selection != null) {
                val individualChanges = mutableSetOf<String>()
                if ("odds" in changes) individualChanges.add("odds")
                if ("active" in changes) individualChanges.add("active")
                if ("shortName" in changes) individualChanges.add("shortName")
                if ("parlay" in changes) individualChanges.add("parlay")

                if (individualChanges.isNotEmpty()) {
                    cell.bindPayload(selection, listOf(individualChanges), selectedId)
                } else {
                    cell.bind(selection, selectedId)
                }
            } else {
                cell.hideView()
            }
        }
    }
}