package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipReserveCompare
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import galaxy.common.proto.Common

class LiveBetSlipReserveAdapter :
    BaseAdapter<Common.ReserveOrder, LiveBetSlipReserveAdapter.LiveBetSlipReserveAdapterViewHolder, AdapterLiveBetSlipReserveBinding>(
        LiveBetSlipReserveCompare()
    ) {
    private val selectionAdapter = LiveBetSlipReserveSelectionAdapter(true)

    inner class LiveBetSlipReserveAdapterViewHolder(binding: AdapterLiveBetSlipReserveBinding) :
        BaseViewHolder(binding) {
        private val nBinding = binding

        fun initManager() {
            val manager = LinearLayoutManager(binding.root.context)
            nBinding.recyclerSelection.also {
                it.layoutManager = manager
                it.adapter = selectionAdapter
                it.itemAnimator = null
            }

        }

        private fun initAdapter(item: Common.ReserveOrderSelection) {
            val list = arrayListOf(item)
            selectionAdapter.submitList(list)
        }


        fun updateData(order: Common.ReserveOrder) {
            initAdapter(getItem(position).selection)

            with(nBinding) {
                val selection = getSelections(order)
                betReserveTvOddsValue.text = selection.odds
                betReserveTvBettingValue.text = order.betAmount
//                TODO 没有对应参数 //预计最高可赢
                betReserveTvExceptValue.text = order.betAmount
            }

        }

    }

    override fun convertPlus(
        holder: LiveBetSlipReserveAdapterViewHolder,
        binding: AdapterLiveBetSlipReserveBinding,
        position: Int
    ) {
        holder.updateData(getItem(position))

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AdapterLiveBetSlipReserveBinding {
        return AdapterLiveBetSlipReserveBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: AdapterLiveBetSlipReserveBinding,
        viewType: Int
    ): LiveBetSlipReserveAdapterViewHolder {
        val holder = LiveBetSlipReserveAdapterViewHolder(binding)
        holder.initManager()
        return holder
    }

    private fun getSelections(order: Common.ReserveOrder) = order.selection

    private fun getMatch(selection: Common.ReserveOrderSelection) = selection.matchBasic
}