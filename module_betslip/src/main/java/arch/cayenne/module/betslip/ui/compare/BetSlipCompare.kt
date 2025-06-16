package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.data.model.ReserveOrderBean

class BetSlipCompare : DiffUtil.ItemCallback<BetSlipData>() {
    override fun areItemsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        return oldItem == newItem
    }

}