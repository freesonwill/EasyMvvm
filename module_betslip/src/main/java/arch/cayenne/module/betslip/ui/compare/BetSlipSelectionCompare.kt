package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData

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