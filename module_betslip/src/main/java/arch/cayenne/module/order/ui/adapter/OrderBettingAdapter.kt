package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingCollapseBinding
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingHeaderBinding
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.viewholder.OrderBettingCollapseViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderBettingHeaderViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderBettingViewHolder

class OrderBettingAdapter(private val type: OrderSportPageEnum, private val listener: OrderEarlySettleListener? = null, private val dataListener: OrderDataSelectorListener? = null): BaseAdapter<BetSlipData, BaseViewHolder, ViewBinding>(
    BetSlipCompare()
) {
    
    // 記錄每個 item 的展開/收起狀態
    private val itemCollapseStates = mutableMapOf<String, Boolean>()
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val viewType = getItemViewType(position)
        when (viewType) {
            HEADER -> {
                val headerHolder = holder as OrderBettingHeaderViewHolder
                val item = getItem(position) as BetSlipOrderHeaderBean
                headerHolder.init(item)
                headerHolder.binding.root.setOnClickListener {
                    dataListener?.onDateClicked()
                }
            }
            BODY_EXPANDED -> {
                val bodyHolder = holder as OrderBettingViewHolder
                val item = getItem(position) as BetSlipOrderBean

                bodyHolder.init(item, type)
                
                // 設置雙擊監聽
                setupDoubleClickListener(bodyHolder.itemView, item.betId, position)

                if (type == OrderSportPageEnum.UNSETTLED) {
                    bodyHolder.getEarlySettleButton().setOnClickListener {
                        listener?.onEarlySettle(item)
                    }
                }
            }
            BODY_COLLAPSED -> {
                val collapseHolder = holder as OrderBettingCollapseViewHolder
                val item = getItem(position) as BetSlipOrderBean

                collapseHolder.init(item, type)
                
                // 設置雙擊監聽
                setupDoubleClickListener(collapseHolder.itemView, item.betId, position)
            }
        }
    }
    
    private fun setupDoubleClickListener(view: android.view.View, betId: String, position: Int) {
        var lastClickTime = 0L
        view.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 300) { // 300ms 內的點擊視為雙擊
                // 切換狀態
                val isCurrentlyCollapsed = itemCollapseStates[betId] ?: false
                itemCollapseStates[betId] = !isCurrentlyCollapsed
                notifyItemChanged(position)
            }
            lastClickTime = currentTime
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            HEADER -> ItemOrderSportBettingHeaderBinding.inflate(inflater, parent, false)
            BODY_COLLAPSED -> ItemOrderSportBettingCollapseBinding.inflate(inflater, parent, false)
            else -> ItemOrderSportBettingBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): BaseViewHolder {
        return when (viewType) {
            HEADER -> OrderBettingHeaderViewHolder(binding as ItemOrderSportBettingHeaderBinding)
            BODY_COLLAPSED -> OrderBettingCollapseViewHolder(binding as ItemOrderSportBettingCollapseBinding)
            else -> OrderBettingViewHolder(binding as ItemOrderSportBettingBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is BetSlipOrderHeaderBean -> HEADER
            is BetSlipOrderBean -> {
                val isCollapsed = itemCollapseStates[item.betId] ?: false
                if (isCollapsed) BODY_COLLAPSED else BODY_EXPANDED
            }

            else -> BODY_EXPANDED
        }
    }

    companion object {
        const val HEADER = 0
        const val BODY_EXPANDED = 1
        const val BODY_COLLAPSED = 2
    }

    interface OrderEarlySettleListener {
        fun onEarlySettle(bean: BetSlipOrderBean)
    }
    interface OrderDataSelectorListener {
        fun onDateClicked()
    }
}