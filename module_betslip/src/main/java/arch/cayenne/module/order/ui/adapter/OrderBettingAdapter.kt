package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingHeaderBinding
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.viewholder.OrderBettingHeaderViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderBettingViewHolder

class OrderBettingAdapter(private val type: OrderSportPageEnum): BaseAdapter<BetSlipData, BaseViewHolder, ViewBinding>(
    BetSlipCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val viewType = getItemViewType(position)
        if (viewType == HEADER) {
            val headerHolder = holder as OrderBettingHeaderViewHolder
            val item = getItem(position) as BetSlipOrderHeaderBean
        } else {
            val bodyHolder = holder as OrderBettingViewHolder
            val item = getItem(position) as BetSlipOrderBean

            bodyHolder.init(item, type)
        }

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return if (viewType == HEADER) {
            ItemOrderSportBettingHeaderBinding.inflate(inflater, parent, false)
        } else {
            ItemOrderSportBettingBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): BaseViewHolder {
        return if (viewType == HEADER) {
            OrderBettingHeaderViewHolder(binding as ItemOrderSportBettingHeaderBinding)
        } else {
            OrderBettingViewHolder(binding as ItemOrderSportBettingBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is BetSlipOrderHeaderBean) {
            HEADER
        } else {
            BODY
        }
    }

    companion object {
        const val HEADER = 0
        const val BODY = 1
    }
}