package com.walisport.module.live.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.databinding.AdapterLiveBetSlipTabLayoutBinding
import galaxy.common.proto.Common

class LiveBetSlipTabAdapter(compare: DiffUtil.ItemCallback<List<Common.Order>>) :
    BaseAdapter<List<Common.Order>, LiveBetSlipTabAdapter.LiveBetSlipTabViewHolder, AdapterLiveBetSlipTabLayoutBinding>(
        compare
    ) {

  inner  class LiveBetSlipTabViewHolder(binding: AdapterLiveBetSlipTabLayoutBinding) :
        BaseViewHolder(binding) {
        val nBinding = binding

        inner class VerticalLinearLayoutManager(
            context: Context?, @RecyclerView.Orientation orientation: Int,
            reverseLayout: Boolean
        ) : LinearLayoutManager(context) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        fun initManager() {
            nBinding.betSlipRecyclerview.setHasFixedSize(true)
            nBinding.betSlipRecyclerview.isNestedScrollingEnabled = false
            val manager = VerticalLinearLayoutManager(
                nBinding.root.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            nBinding.betSlipRecyclerview.layoutManager = manager
        }

        fun initAdapter(list: List<Common.Order>) {
            val adapter = LiveBetSlipAdapter()
            nBinding.betSlipRecyclerview.adapter = adapter

        }
    }

    override fun convertPlus(
        holder: LiveBetSlipTabViewHolder,
        binding: AdapterLiveBetSlipTabLayoutBinding,
        position: Int
    ) {
        holder.initAdapter(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AdapterLiveBetSlipTabLayoutBinding {
        val binding = AdapterLiveBetSlipTabLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: AdapterLiveBetSlipTabLayoutBinding,
        viewType: Int
    ): LiveBetSlipTabViewHolder {
        val holder = LiveBetSlipTabViewHolder(binding)
        holder.initManager()
        return holder
    }
}