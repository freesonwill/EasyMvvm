package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipReserveBean

class BetSlipCompare : DiffUtil.ItemCallback<BetSlipData>() {
    override fun areItemsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return when {
            oldItem is BetSlipOrderBean && newItem is BetSlipOrderBean ->
                oldItem.betId == newItem.betId

            oldItem is BetSlipReserveBean && newItem is BetSlipReserveBean ->
                oldItem.reserveId == newItem.reserveId

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return oldItem == newItem
    }

}