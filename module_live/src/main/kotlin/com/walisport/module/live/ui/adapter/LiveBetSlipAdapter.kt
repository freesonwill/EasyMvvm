package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipCompare
import galaxy.common.proto.Common

class LiveBetSlipAdapter :
    BaseAdapter<Common.Order, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        LiveBetSlipCompare()
    ) {

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {}

    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): LiveBetSlipViewHolder {
    }
}