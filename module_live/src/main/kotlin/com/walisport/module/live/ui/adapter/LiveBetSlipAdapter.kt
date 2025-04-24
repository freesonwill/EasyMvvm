package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.compare.LiveBetSlipCompare
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order
import org.w3c.dom.Text

class LiveBetSlipAdapter(type: LiveBetSlipEnum) :
    BaseAdapter<Common.Order, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        LiveBetSlipCompare()
    ) {
    private val betSlipType = type
    private val METHOD_INIT = 0
    private val METHOD_UPDATE = 1
    private val METHOD_ADAPTER = 2


    private fun updateInvalid(
        item: Order,
        bind: AdapterLiveBetSlipInvalidBinding,
        isReserve: Boolean
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
//                tvUnit1Value.text = item.betId //预约赔率
//                tvUnit2Value.text = item.odds  //预约投注
//                tvUnit3Value.text = item.betAmount //预约最高可赢
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


    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {
        holder.managerView(METHOD_ADAPTER,position,getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (betSlipType) {
            LiveBetSlipEnum.UnSettled -> AdapterLiveBetSlipUnsettleBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Confirming -> AdapterLiveBetSlipConfirmBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Settled -> AdapterLiveBetSlipSettledBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Reserve -> AdapterLiveBetSlipReserveBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Invalid -> AdapterLiveBetSlipInvalidBinding.inflate(
                inflater,
                parent,
                false
            )
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): LiveBetSlipViewHolder {
        val holder = LiveBetSlipViewHolder(binding)
        holder.managerView(METHOD_INIT, -1)
        return holder
    }

    private fun getSelections(order: Common.Order) = order.selectionsList

    private fun getMatch(selection: Common.OrderSelection) = selection.matchBasic

    private fun getEarlySettlePrice(order: Order) = order.earlySettlePrice

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        fun managerView(method: Int, position: Int, item: Order? = null) {
            when (binding) {
                is AdapterLiveBetSlipUnsettleBinding -> {
                    val nBinding = binding as AdapterLiveBetSlipUnsettleBinding
                    when (method) {
                        METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                        METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                        METHOD_UPDATE -> item?.let { nItem ->
                            nBinding.also {
                                updateData(
                                    nItem,
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
                    val nBinding = binding as AdapterLiveBetSlipConfirmBinding
                    when (method) {
                        METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                        METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                        METHOD_UPDATE -> item?.let { nItem ->
                            nBinding.also {
                                updateData(
                                    nItem,
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
                    val nBinding = binding as AdapterLiveBetSlipSettledBinding
                    when (method) {
                        METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                        METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                        METHOD_UPDATE -> item?.let { nItem ->
                            nBinding.also {
                                updateData(
                                    nItem,
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
                    val nBinding = binding as AdapterLiveBetSlipInvalidBinding
                    when (method) {
                        METHOD_INIT -> initRecycler(nBinding.recyclerSelection)
                        METHOD_ADAPTER -> initAdapter(nBinding.recyclerSelection)
                        METHOD_UPDATE -> item?.let { updateInvalid(item, nBinding, false) }
                        else -> {}
                    }
                }

                is AdapterLiveBetSlipReserveBinding -> {
                }
            }
        }

        fun initRecycler(recyclerView: RecyclerView) {
            val manager = LinearLayoutManager(binding.root.context)
            val adapter = LiveBetSlipSelectionAdapter(betSlipType)
            recyclerView.also {
                it.layoutManager = manager
                it.itemAnimator = null
                it.adapter = adapter
            }
        }

        fun initAdapter(recyclerView: RecyclerView) {
            val list = arrayListOf(
                Common.OrderSelection.newBuilder().build(),
                Common.OrderSelection.newBuilder().build(),
                Common.OrderSelection.newBuilder().build()
            )
            recyclerView.adapter?.let {
                val adapter = it as LiveBetSlipSelectionAdapter
                adapter.submitList(list)
            }
        }


        private fun updateData(
            order: Order,
            tvCode: TextView? = null,
            tvOdds: TextView? = null,
            tvBet: TextView? = null,
            tvExpectMaxWin: TextView? = null,
            tvPartEarlySettled: TextView? = null,
            tvWinLoseAmount: TextView? = null
        ) {
            tvCode?.text = order.betId
            tvOdds?.text = order.odds
            tvBet?.text = order.betAmount
//            TODO 预计最高可赢
            tvExpectMaxWin?.text = order.returnAmount
            tvPartEarlySettled?.text = order.earlySettlePrice.price
            tvWinLoseAmount?.text = "0" // order.betAmount-order.returnAmount
        }
    }

}