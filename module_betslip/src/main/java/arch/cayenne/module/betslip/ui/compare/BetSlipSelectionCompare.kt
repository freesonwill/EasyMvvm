package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData

class BetSlipSelectionCompare : DiffUtil.ItemCallback<BetSlipSelectionData>() {
    override fun areItemsTheSame(
        oldItem: BetSlipSelectionData,
        newItem: BetSlipSelectionData
    ): Boolean {
        if (oldItem.selection != null) {
            return oldItem.selection.selectionId == newItem.selection?.selectionId
        } else if (oldItem.reserve != null) {
            return oldItem.reserve.selectionId == newItem.selection?.selectionId
        }
        return true
    }

    override fun areContentsTheSame(
        oldItem: BetSlipSelectionData,
        newItem: BetSlipSelectionData
    ): Boolean {
        if (oldItem.selection != null) {
            return oldItem.selection.selectionId == newItem.selection?.selectionId
        } else if (oldItem.reserve != null) {
            return oldItem.reserve.selectionId == newItem.selection?.selectionId
        }
        return true
    }

}