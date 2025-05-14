package arch.cayenne.module.betslip.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.betslip.data.model.LiveBetSlipData

class LiveBetSlipCompare : DiffUtil.ItemCallback<LiveBetSlipData>() {
    override fun areItemsTheSame(oldItem: LiveBetSlipData, newItem: LiveBetSlipData): Boolean {
        if(oldItem.order != null){
           return oldItem.order.betId == newItem.order?.betId
        }else if(oldItem.reserve != null){
            return oldItem.reserve.reserveId == newItem.reserve?.reserveId
        }
        return  false
    }

    override fun areContentsTheSame(oldItem: LiveBetSlipData, newItem: LiveBetSlipData): Boolean {
        if(oldItem.order != null){
            return oldItem.order.betId == newItem.order?.betId
        }else if(oldItem.reserve != null){
            return oldItem.reserve.reserveId == newItem.reserve?.reserveId
        }
        return  false
    }

}