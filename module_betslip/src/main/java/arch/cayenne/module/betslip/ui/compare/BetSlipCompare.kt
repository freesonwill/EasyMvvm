package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.BetSlipData

class BetSlipCompare : DiffUtil.ItemCallback<BetSlipData>() {
    override fun areItemsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        if(oldItem.order != null){
           return oldItem.order.betId == newItem.order?.betId
        }else if(oldItem.reserve != null){
            return oldItem.reserve.reserveId == newItem.reserve?.reserveId
        }
        return  false
    }

    override fun areContentsTheSame(oldItem: BetSlipData, newItem: BetSlipData): Boolean {
        if(oldItem.order != null){
            return oldItem.order.betId == newItem.order?.betId
        }else if(oldItem.reserve != null){
            return oldItem.reserve.reserveId == newItem.reserve?.reserveId
        }
        return  false
    }

}