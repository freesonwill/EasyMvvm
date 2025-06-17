package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.BetSlipSelectionData

class BetSlipSelectionCompare : DiffUtil.ItemCallback<BetSlipSelectionData>() {
    override fun areItemsTheSame(
        oldItem: BetSlipSelectionData,
        newItem: BetSlipSelectionData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: BetSlipSelectionData,
        newItem: BetSlipSelectionData
    ): Boolean {
        return oldItem == newItem
    }

}