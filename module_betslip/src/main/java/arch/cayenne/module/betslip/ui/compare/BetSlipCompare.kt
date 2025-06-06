package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.BetSlipReserve

class BetSlipCompare : DiffUtil.ItemCallback<BetSlipData>() {
    override fun areItemsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return when {
            oldItem is BetSlipOrder && newItem is BetSlipOrder ->
                oldItem.order.betId == newItem.order.betId

            oldItem is BetSlipReserve && newItem is BetSlipReserve ->
                oldItem.reserve.reserveId == newItem.reserve.reserveId

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return oldItem == newItem
    }

}