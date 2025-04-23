package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipCompare
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class LiveBetSlipAdapter(type: LiveBetSlipEnum) :
    BaseAdapter<Common.Order, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        LiveBetSlipCompare()
    ) {
    private val betSlipType = type
    private val selectionAdapter = LiveBetSlipSelectionAdapter(betSlipType, true)

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        fun initManager() {
            val manager = LinearLayoutManager(binding.root.context)

            when (binding) {
                is AdapterLiveBetSlipUnsettleBinding -> {
                    val nBinding = binding as AdapterLiveBetSlipUnsettleBinding
                    nBinding.recyclerSelection.also {
                        it.layoutManager = manager
                        it.adapter = selectionAdapter
                    }
                }

                is AdapterLiveBetSlipConfirmBinding -> {
                    val nBinding = binding as AdapterLiveBetSlipConfirmBinding
                    nBinding.recyclerSelection.also {
                        it.layoutManager = manager
                        it.adapter = selectionAdapter
                    }
                }

                is AdapterLiveBetSlipSettledBinding -> {
                    val nBinding = binding as AdapterLiveBetSlipSettledBinding
                    nBinding.recyclerSelection.also {
                        it.layoutManager = manager
                        it.adapter = selectionAdapter
                    }
                }

                is AdapterLiveBetSlipReserveBinding -> {
                    val nBinding = binding as AdapterLiveBetSlipReserveBinding
                    nBinding.recyclerSelection.also {
                        it.layoutManager = manager
                        it.adapter = selectionAdapter
                    }
                }

                is AdapterLiveBetSlipInvalidBinding -> {
                    val nBinding = binding as AdapterLiveBetSlipInvalidBinding
                    nBinding.recyclerSelection.also {
                        it.layoutManager = manager
                        it.adapter = selectionAdapter
                    }
                }
            }
        }

        fun initAdapter() {
            val list = arrayListOf(
                Common.OrderSelection.newBuilder().build(),
                Common.OrderSelection.newBuilder().build(),
                Common.OrderSelection.newBuilder().build()
            )
            selectionAdapter.submitList(list)
        }


        fun updateUnSettle(order: Common.Order) {
            val nBinding = binding as AdapterLiveBetSlipUnsettleBinding
            with(nBinding) {
                val selection = getSelections(order).first()
                val match = getMatch(selection)
//                Glide.with(root.context).load(match.tournamentIcon).into(betUnsettledIvBall)
//                betUnsettledTvRace.text = match.matchName
//                betUnsettledTvIntroduce.text = selection.selectionName
//                betUnsettledTvStatus.text = selection.marketName
//                betUnsettledTvStart.text =  "${match.startTime}"
//                betUnsettledTvAodds.text = selection.odds
//
//                betUnsettledTvBetcodeValue.text = order.betId
//                betUnsettledTvOddsValue.text = order.odds
//                betUnsettledTvBettingValue.text = order.betAmount
//                betUnsettledTvExceptValue.text = order.returnAmount
            }

        }

    }

    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {
        holder.initAdapter()

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
        holder.initManager()
        return holder
    }

    private fun getSelections(order: Common.Order) = order.selectionsList

    private fun getMatch(selection: Common.OrderSelection) = selection.matchBasic

    private fun getEarlySettlePrice(order: Order) = order.earlySettlePrice

}