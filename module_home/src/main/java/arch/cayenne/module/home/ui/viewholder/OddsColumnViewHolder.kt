package arch.cayenne.module.home.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemOddsColumnBinding

class OddsColumnViewHolder(
    mBinding: ItemOddsColumnBinding, // odds_row_item.xml 的 binding
    onOddsClick: (SelectionBeanLite, Boolean) -> Unit
) : BaseViewHolder(mBinding) {

    private val oddsCells = listOf(
        OddsCellViewHolder(mBinding.itemOddsCell1, onOddsClick),
        OddsCellViewHolder(mBinding.itemOddsCell2, onOddsClick),
        OddsCellViewHolder(mBinding.itemOddsCell3, onOddsClick)
    )

    fun bind(market: MarketBeanLite, selections: List<SelectionBeanLite>) {
        val forceLocked = market.defaultSelectionCount == 0 // 判斷是否要強制鎖盤
        if (forceLocked) {
            oddsCells.forEachIndexed { index, cell ->
                selections.getOrNull(index)?.let {
                    cell.deActivate()
                }
            }
        } else {
            oddsCells.forEachIndexed { index, cell ->
                selections.getOrNull(index)?.let {
                    cell.bind(it)
                } ?: cell.hideView()
            }
        }
    }

    fun bindPayload(
        selections: List<SelectionBeanLite>,
        payloads: List<Any>
    ) {
        val changes = payloads.firstOrNull() as? Set<*> ?: return
        oddsCells.forEachIndexed { index, cell ->
            val selection = selections.getOrNull(index)
            if (selection != null) {
                val individualChanges = mutableSetOf<String>()
                if ("odds" in changes) individualChanges.add("odds")
                if ("active" in changes) individualChanges.add("active")
                if ("shortName" in changes) individualChanges.add("shortName")
                if ("parlay" in changes) individualChanges.add("parlay")
                if ("isSelected" in changes) individualChanges.add("isSelected")
                if ("trend" in changes) individualChanges.add("trend")

                if (individualChanges.isNotEmpty()) {
                    cell.bindPayload(selection, listOf(individualChanges))
                } else {
                    cell.bind(selection)
                }
            } else {
                cell.hideView()
            }
        }
    }
}