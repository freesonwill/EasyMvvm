package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingCollapseBinding
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingHeaderBinding
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.viewholder.OrderBettingCollapseViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderBettingHeaderViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderBettingViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderReserveViewHolder
import arch.cayenne.module.order.ui.viewholder.OrderReserveCollapseViewHolder

class OrderBettingAdapter(
    private val type: OrderSportPageEnum,
    private val earlySettleListener: OrderEarlySettleListener? = null,
    private val dataListener: OrderDataSelectorListener? = null,
    private val reserveListener: OrderReserveListener? = null,
    private val selectionListener: SelectionItemListener? = null
) : BaseAdapter<BetSlipData, BaseViewHolder, ViewBinding>(BetSlipCompare()) {

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
                if (type == OrderSportPageEnum.RESERVE) {
                    val reserveHolder = holder as OrderReserveViewHolder
                    val item = getItem(position) as BetSlipReserveBean

                    reserveHolder.init(item)
                    reserveHolder.initSelection(listOf(item.selection), type, selectionListener)
                    reserveHolder.setDoubleClick {
                        toggleItemState(item.reserveId, position)
                    }
                    reserveHolder.setCancelButtonClickListener {
                        reserveListener?.onCancelReserve(item)
                    }
                    reserveHolder.setModifyButtonClickListener { x, y, h ->
                        reserveListener?.onModifyReserve(
                            item,
                            x, y, h
                        )
                    }
                } else {
                    val orderHolder = holder as OrderBettingViewHolder
                    val item = getItem(position) as BetSlipOrderBean

                    orderHolder.init(item)
                    orderHolder.initSelection(item.selectionsList, type, selectionListener)
                    orderHolder.setDoubleClick {
                        toggleItemState(item.betId, position)
                    }

                    if (type == OrderSportPageEnum.UNSETTLED) {
                        orderHolder.setEarlySettleButtonClickListener {
                            earlySettleListener?.onEarlySettle(item)
                        }
                    }
                }
            }

            BODY_COLLAPSED -> {
                if (type == OrderSportPageEnum.RESERVE) {
                    val reserveCollapseHolder = holder as OrderReserveCollapseViewHolder
                    val item = getItem(position) as BetSlipReserveBean

                    reserveCollapseHolder.init(item)
                    reserveCollapseHolder.setDoubleClick {
                        toggleItemState(item.reserveId, position)
                    }
                } else {
                    val collapseHolder = holder as OrderBettingCollapseViewHolder
                    val item = getItem(position) as BetSlipOrderBean

                    collapseHolder.init(item)
                    collapseHolder.setDoubleClick {
                        toggleItemState(item.betId, position)
                    }
                }
            }

        }
    }

    private fun toggleItemState(id: String, position: Int) {
        val isCurrentlyCollapsed = itemCollapseStates[id] ?: false
        itemCollapseStates[id] = !isCurrentlyCollapsed
        notifyItemChanged(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            HEADER -> ItemOrderSportBettingHeaderBinding.inflate(inflater, parent, false)
            BODY_COLLAPSED -> ItemOrderSportBettingCollapseBinding.inflate(inflater, parent, false)
            BODY_EXPANDED -> ItemOrderSportBettingBinding.inflate(inflater, parent, false)
            else -> ItemOrderSportBettingBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): BaseViewHolder {
        return when (viewType) {
            HEADER -> OrderBettingHeaderViewHolder(binding as ItemOrderSportBettingHeaderBinding)
            BODY_COLLAPSED -> {
                if (type == OrderSportPageEnum.RESERVE) {
                    OrderReserveCollapseViewHolder(binding as ItemOrderSportBettingCollapseBinding)
                } else {
                    OrderBettingCollapseViewHolder(binding as ItemOrderSportBettingCollapseBinding)
                }
            }

            BODY_EXPANDED -> {
                if (type == OrderSportPageEnum.RESERVE) {
                    OrderReserveViewHolder(binding as ItemOrderSportBettingBinding)
                } else {
                    OrderBettingViewHolder(binding as ItemOrderSportBettingBinding)
                }
            }

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

            is BetSlipReserveBean -> {
                val isCollapsed = itemCollapseStates[item.reserveId] ?: false
                if (isCollapsed) BODY_COLLAPSED else BODY_EXPANDED
            }

            else -> BODY_EXPANDED
        }
    }

    fun setItemClickListener(listener: RecyclerItemListener<BetSlipOrderBean>){

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

    interface OrderReserveListener {
        fun onCancelReserve(bean: BetSlipReserveBean)
        fun onModifyReserve(
            bean: BetSlipReserveBean,
            locationX: Int,
            locationY: Int,
            viewHeight: Int
        )
    }

    interface SelectionItemListener {
        fun onSingleClick(bean: BetSlipSelectionData)
    }
}