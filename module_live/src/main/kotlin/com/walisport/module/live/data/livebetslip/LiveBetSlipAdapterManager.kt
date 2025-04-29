package com.walisport.module.live.data.livebetslip

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.walisport.module.live.R
import com.walisport.module.live.data.OrderStatusEnum
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipSelectionAdapter
import com.walisport.module.live.utils.LiveBetSlipUtils.calculateMaxWin
import com.walisport.module.live.utils.LiveBetSlipUtils.winOrLoseAmount
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class LiveBetSlipAdapterManager(binding: ViewBinding, type: LiveBetSlipEnum) {

    private val METHOD_INIT = 0
    private val METHOD_UPDATE = 1
    private val METHOD_ADAPTER = 2
    private val binding: ViewBinding = binding
    private val betSlipType = type

    fun init() {
        managerView(METHOD_INIT, -1, null)
    }

    fun updateView(position: Int, item: LiveBetSlipData) {
        managerView(METHOD_UPDATE, position, item)
    }

    fun updateAdapter(position: Int, item: LiveBetSlipData) {
        managerView(METHOD_ADAPTER, position, item)
    }


    private fun managerView(method: Int, position: Int, item: LiveBetSlipData? = null) {

        when (binding) {
            is AdapterLiveBetSlipUnsettleBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                    METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                    METHOD_UPDATE -> item?.order?.let {
                        nBinding.also {
                            updateData(
                                item.order,
                                tvCode = it.betUnsettledTvBetcodeValue,
                                tvOdds = it.betUnsettledTvOddsValue,
                                tvBet = it.betUnsettledTvBettingValue,
                                tvExpectMaxWin = it.betUnsettledTvExceptValue
                            )
                        }
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipConfirmBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                    METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                    METHOD_UPDATE -> item?.order?.let {
                        nBinding.also {
                            updateData(
                                item.order,
                                tvCode = it.betConfirmTvBetcodeValue,
                                tvOdds = it.betConfirmTvOddsValue,
                                tvBet = it.betConfirmTvBettingValue,
                                tvExpectMaxWin = it.betConfirmTvExceptValue
                            )
                        }
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipSettledBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                    METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                    METHOD_UPDATE -> item?.order?.let {
                        nBinding.also {
                            updateData(
                                item.order,
                                tvCode = it.betSettledTvBetcodeValue,
                                tvOdds = it.betSettledTvOddsValue,
                                tvBet = it.betSettledTvBettingValue,
                                tvExpectMaxWin = it.betSettledTvExceptValue
                            )
                        }
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipInvalidBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                    METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                    METHOD_UPDATE -> item?.order?.let {
                        updateInvalid(item.order, nBinding, false)
                    }

                    else -> {}
                }
            }

            is AdapterLiveBetSlipReserveBinding -> {
                val nBinding = binding
                when (method) {
                    METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                    METHOD_ADAPTER -> initReserveAdapter(
                        nBinding.recyclerSelection,
                        item?.reserve!!
                    )

                    METHOD_UPDATE -> item?.reserve?.let {
                        updateReserveData(item.reserve, nBinding)
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * 投注单RecyclerView
     * */
    private fun initRecycler(recyclerView: RecyclerView) {
        val manager = LinearLayoutManager(binding.root.context)
        val adapter = LiveBetSlipSelectionAdapter(betSlipType)
        recyclerView.also {
            it.layoutManager = manager
            it.itemAnimator = null
            it.adapter = adapter
        }
    }

    /**
     * 投注单列表展示
     * */
    private fun initAdapter(recyclerView: RecyclerView) {
        val list = arrayListOf(
            LiveBetSlipSelectionData(Common.OrderSelection.newBuilder().build()),
            LiveBetSlipSelectionData(Common.OrderSelection.newBuilder().build()),
            LiveBetSlipSelectionData(Common.OrderSelection.newBuilder().build()),
        )
        recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipSelectionAdapter
            adapter.submitList(list)
        }
    }

    /**
     * 预约单列表展示
     * */
    private fun initReserveAdapter(recyclerView: RecyclerView, reserve: Common.ReserveOrder) {

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
        tvStatus: TextView? = null
    ) {
        tvCode?.text = order.betId
        tvOdds?.text = order.odds
        tvBet?.text = order.betAmount
        tvExpectMaxWin?.text = calculateMaxWin(order.odds, order.betAmount).toString()
        tvPartEarlySettled?.text = order.earlySettlePrice.price
        tvWinLoseAmount?.text = winOrLoseAmount(
            order.betAmount, order.returnAmount
        ).toString() // order.betAmount-order.returnAmount
        tvStatus?.let {
            val status = OrderStatusEnum.getStatus(order.status)
            status?.let { st ->
                it.background = ContextCompat.getDrawable(it.context, st.resId)
            }
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
                tvUnit1.text = ContextCompat.getString(bind.root.context, R.string.live_bet_bet_num)
                tvUnit2.text = ContextCompat.getString(bind.root.context, R.string.live_bet_odds)
                tvUnit3.text = ContextCompat.getString(bind.root.context, R.string.live_bet_on)
                tvUnit4.text =
                    ContextCompat.getString(bind.root.context, R.string.live_bet_except_max_win)
                tvUnit1Value.text = item.betId
                tvUnit2Value.text = item.odds
                tvUnit3Value.text = item.betAmount
//            TODO 预计最高可赢
                tvUnit4Value.text = item.returnAmount
            }
        }
    }


    /**
     * 预约单数据更新
     * */
    private fun updateReserveData(
        order: Common.ReserveOrder,
        nBinding: AdapterLiveBetSlipReserveBinding
    ) {
        with(nBinding) {
            val selection = order.selection
            betReserveTvOddsValue.text = selection.odds
            betReserveTvBettingValue.text = order.betAmount
            betReserveTvExceptValue.text =
                calculateMaxWin(
                    order.selection.odds, order.betAmount
                ).toString()
        }
    }

}