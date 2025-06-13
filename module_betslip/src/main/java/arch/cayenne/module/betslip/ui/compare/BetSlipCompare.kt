package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.data.model.ReserveOrderBean

class BetSlipCompare : DiffUtil.ItemCallback<BetSlipData>() {
    override fun areItemsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return when {
            oldItem is BetSlipOrderBean && newItem is BetSlipOrderBean ->
                oldItem.betId == newItem.betId

            oldItem is ReserveOrderBean && newItem is ReserveOrderBean ->
                oldItem.reserveId == newItem.reserveId

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return oldItem == newItem
    }

}