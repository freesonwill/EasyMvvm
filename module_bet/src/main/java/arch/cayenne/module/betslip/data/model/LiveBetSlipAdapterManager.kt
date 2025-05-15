package arch.cayenne.module.betslip.data.model

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.bet.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.bet.databinding.ItemTipsLayoutBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum
import arch.cayenne.module.betslip.data.constants.LiveBetSlipResultOrderStatusEnum
import arch.cayenne.module.betslip.ui.adapter.LiveBetSlipSelectionAdapter
import arch.cayenne.module.betslip.utisl.LiveBetSlipUtils.earlySettlePrice
import arch.cayenne.module.betslip.utisl.LiveBetSlipUtils.expectMaxAmount
import arch.cayenne.module.betslip.utisl.LiveBetSlipUtils.winOrLoseAmount
import arch.cayenne.module.betslip.utisl.RecyclerItemListener
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class LiveBetSlipAdapterManager(binding: ViewBinding, type: arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum) {

    private val METHOD_INIT = 0
    private val METHOD_UPDATE = 1
    private val binding: ViewBinding = binding
    private val betSlipType = type
    private var expandedListener: RecyclerItemListener<LiveBetSlipExpandedEnum>? = null

    fun init(listener: RecyclerItemListener<arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum>) {
        this.expandedListener = listener
        managerView(METHOD_INIT, -1)
    }

    fun updateView(position: Int, item: LiveBetSlipData) {
        managerView(METHOD_UPDATE, position, item)
    }

    private fun managerView(
        method: Int,
        position: Int,
        item: LiveBetSlipData? = null,
    ) {

        when (binding) {
            is AdapterLiveBetSlipUnsettleBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initView(nBinding.recyclerSelection)
                    METHOD_UPDATE -> item?.order?.let {
                        nBinding.also {
                            updateData(
                                item.order,
                                tvCode = it.betUnsettledTvBetcodeValue,
                                tvOdds = it.betUnsettledTvOddsValue,
                                tvBet = it.betUnsettledTvBettingValue,
                                tvExpectMaxWin = it.betUnsettledTvExceptValue,
                                tvEarlySettle = it.betUnsettledTvEarlySettle
                            )
                            submitAdapter(
                                it.recyclerSelection, item, position
                            )
                            configView(
                                item.order,
                                position,
                                tvEarlySettle = it.betUnsettledBtSettle
                            )
                        }
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipConfirmBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initView(nBinding.recyclerSelection, nBinding.ivTip)
                    METHOD_UPDATE -> item?.order?.let {
                        nBinding.also {
                            updateData(
                                item.order,
                                tvCode = it.betConfirmTvBetcodeValue,
                                tvOdds = it.betConfirmTvOddsValue,
                                tvBet = it.betConfirmTvBettingValue,
                                tvExpectMaxWin = it.betConfirmTvExceptValue
                            )
                            submitAdapter(
                                it.recyclerSelection, item, position
                            )
                        }

                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipSettledBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initView(nBinding.recyclerSelection, nBinding.ivTip)
                    METHOD_UPDATE -> item?.order?.let {
                        nBinding.also {
                            updateData(
                                item.order,
                                tvCode = it.betSettledTvBetcodeValue,
                                tvOdds = it.betSettledTvOddsValue,
                                tvBet = it.betSettledTvBettingValue,
                                tvExpectMaxWin = it.betSettledTvExceptValue
                            )
                            submitAdapter(
                                it.recyclerSelection, item, position
                            )
                        }
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipInvalidBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initView(nBinding.recyclerSelection)
                    METHOD_UPDATE -> item?.order?.let {
                        updateInvalid(item.order, nBinding, false)
                        submitAdapter(
                            nBinding.recyclerSelection, item, position
                        )
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipReserveBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initView(nBinding.recyclerSelection)

                    METHOD_UPDATE -> item?.reserve?.let {
                        updateReserveData(position, item.reserve, nBinding)
                        submitReserveAdapter(nBinding.recyclerSelection, item.reserve)
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * 投注单RecyclerView
     * */
    private fun initView(recyclerView: RecyclerView, ivTip: ImageView? = null) {
        val manager = LinearLayoutManager(binding.root.context)
        val adapter = LiveBetSlipSelectionAdapter(
            betSlipType,
            object : RecyclerItemListener<LiveBetSlipExpandedEnum> {
                override fun onItemClick(item: LiveBetSlipExpandedEnum?, position: Int) {
                    expandedListener?.onItemClick(null, position)
                }
            })
        recyclerView.also {
            it.layoutManager = manager
            it.itemAnimator = null
            it.adapter = adapter
        }
        ivTip?.let {
            it.clickNoRepeat {
                showBetTip(ivTip)
            }
        }
    }

    /**
     * 投注单列表展示
     * */
    private fun submitAdapter(
        recyclerView: RecyclerView, data: LiveBetSlipData, position: Int
    ) {
        var list =
            data.order!!.selectionsList.map { LiveBetSlipSelectionData(selection = it) }.toList()
        recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipSelectionAdapter
            adapter.updateBasicData(data.expandedEnum, position)
            if (list.size > 3 && data.expandedEnum == arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum.Fold) {
                list = list.subList(0, 2)
            }
            adapter.currentList.clear()
            adapter.submitList(list)
        }
    }

    /**
     * 预约单列表展示
     * */
    private fun submitReserveAdapter(
        recyclerView: RecyclerView,
        reserve: Common.ReserveOrder,
    ) {
        val list = arrayListOf(LiveBetSlipSelectionData(reserve = reserve.selection))
        recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipSelectionAdapter
            adapter.submitList(list)
        }
    }


    /**
     * 未结算 确认中 已结算 更新数据
     * */
    private fun updateData(
        order: Order,
        tvCode: TextView? = null,
        tvOdds: TextView? = null,
        tvBet: TextView? = null,
        tvExpectMaxWin: TextView? = null,
        tvPartEarlySettled: TextView? = null,
        tvWinLoseAmount: TextView? = null,
        tvStatus: TextView? = null,
        tvEarlySettle: TextView? = null
    ) {
        tvCode?.text = order.betId
        tvOdds?.text = order.odds
        tvBet?.text = order.betAmount
        tvExpectMaxWin?.text = expectMaxAmount(order.betAmount,order.odds)
        tvPartEarlySettled?.text = order.earlySettlePrice.price
        tvWinLoseAmount?.text = winOrLoseAmount(order)
        tvStatus?.let {
            val status = LiveBetSlipResultOrderStatusEnum.getStatus(order.status)
            status?.let { st ->
                it.background = ContextCompat.getDrawable(it.context, st.resId)
            }
        }
        tvEarlySettle?.let {
            it.text = "$"+earlySettlePrice(order.betAmount,order.earlySettlePrice.price,order.earlyBetAmount)
        }
    }

    /**
     * 失效更新数据
     * */
    private fun updateInvalid(
        item: Order, bind: AdapterLiveBetSlipInvalidBinding, isReserve: Boolean
    ) {
        with(bind) {
            if (isReserve) {
                betExpiredTvStatus.width = 60.dp2px
                betExpiredTvStatus.text =
                    root.context.resources.getString(R.string.live_bet_reserve_expired)
                betExpiredTvStatus.setBackgroundResource(
                    SkinnableResourceManager.getTargetResourceId(
                        root.context,
                        R.drawable.bg_reser_expired
                    )
                )
                tvUnit1.text =
                    ContextCompat.getString(bind.root.context, R.string.live_bet_reserve_odds)
                tvUnit2.text =
                    ContextCompat.getString(bind.root.context, R.string.live_bet_reserve_bet)
                tvUnit3.text =
                    ContextCompat.getString(bind.root.context, R.string.live_bet_except_max_win)
                tvUnit4.isVisible = false
                tvUnit4Value.isVisible = false

                tvUnit1Value.text = item.betId //预约赔率
                tvUnit2Value.text = item.odds  //预约投注
                tvUnit3Value.text = item.betAmount //预约最高可赢
            } else {
                betExpiredTvStatus.width = 34.dp2px
                betExpiredTvStatus.text =
                    root.context.resources.getString(R.string.live_bet_rejection)
                betExpiredTvStatus.setBackgroundResource(
                    SkinnableResourceManager.getTargetResourceId(
                        root.context,
                        R.drawable.bg_rejection
                    )
                )
                tvUnit1.text = ContextCompat.getString(bind.root.context, R.string.live_bet_bet_num)
                tvUnit2.text = ContextCompat.getString(bind.root.context, R.string.live_bet_odds)
                tvUnit3.text = ContextCompat.getString(bind.root.context, R.string.live_bet_on)
                tvUnit4.text =
                    ContextCompat.getString(bind.root.context, R.string.live_bet_except_max_win)
                tvUnit1Value.text = item.betId
                tvUnit2Value.text = item.odds
                tvUnit3Value.text = item.betAmount
                tvUnit4Value.text = expectMaxAmount(item.betAmount,item.odds)
            }
        }
    }


    /**
     * 预约单数据更新
     * */
    private fun updateReserveData(
        position: Int, order: Common.ReserveOrder, nBinding: AdapterLiveBetSlipReserveBinding
    ) {
        with(nBinding) {
            val selection = order.selection
            betReserveTvOddsValue.text = selection.odds
            betReserveTvBettingValue.text = order.betAmount
            betReserveTvExceptValue.text = expectMaxAmount(order.betAmount,order.selection.odds)
            betReserveBtCancel.tag = position
            betReserveBtModify.tag = position
        }
    }

    private fun configView(order: Order, position: Int, tvEarlySettle: LinearLayout? = null) {
        tvEarlySettle?.let {
//            it.isVisible = order.earlySupport
            it.tag = position
        }
    }

    private fun showBetTip(attachView: View) {
        val pop = PopupWindow(attachView.context)
        pop.contentView =
            ItemTipsLayoutBinding.inflate(LayoutInflater.from(attachView.context)).root
        pop.isOutsideTouchable = true
        pop.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    attachView.context,
                    arch.cayenne.lib.common.R.color.tran_0
                )
            )
        )
        pop.showAsDropDown(attachView)
    }
}