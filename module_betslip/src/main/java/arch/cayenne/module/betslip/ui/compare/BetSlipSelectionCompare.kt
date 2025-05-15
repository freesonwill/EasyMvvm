package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData

class BetSlipSelectionCompare : DiffUtil.ItemCallback<LiveBetSlipSelectionData>() {
    override fun areItemsTheSame(
        oldItem: LiveBetSlipSelectionData,
        newItem: LiveBetSlipSelectionData
    ): Boolean {
        if (oldItem.selection != null) {
            return oldItem.selection.selectionId == newItem.selection?.selectionId
        } else if (oldItem.reserve != null) {
            return oldItem.reserve.selectionId == newItem.selection?.selectionId
        }
        return true
    }

    override fun areContentsTheSame(
        oldItem: LiveBetSlipSelectionData,
        newItem: LiveBetSlipSelectionData
    ): Boolean {
        if (oldItem.selection != null) {
            return oldItem.selection.selectionId == newItem.selection?.selectionId
        } else if (oldItem.reserve != null) {
            return oldItem.reserve.selectionId == newItem.selection?.selectionId
        }
        return true
    }

}