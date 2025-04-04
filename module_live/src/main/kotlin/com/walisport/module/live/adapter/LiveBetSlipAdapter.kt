package com.walisport.module.live.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.walisport.lib.common.ui.adapter.BaseAdapter
import com.walisport.lib.common.ui.adapter.BaseViewHolder
import com.walisport.module.live.compare.LiveBetSlipMatchCompare
import com.walisport.module.live.data.model.LiveBetSlipBean
import com.walisport.module.live.databinding.AdapterBetslipUnsettledLayoutBinding

class LiveBetSlipAdapter :
    BaseAdapter<LiveBetSlipBean, LiveBetSlipAdapter.ViewHolder, AdapterBetslipUnsettledLayoutBinding>(
        compare = LiveBetSlipMatchCompare()
    ) {


    inner class ViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {}

    override fun convertPlus(
        holder: ViewHolder,
        binding: AdapterBetslipUnsettledLayoutBinding,
        position: Int
    ) {

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AdapterBetslipUnsettledLayoutBinding {
        return AdapterBetslipUnsettledLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: AdapterBetslipUnsettledLayoutBinding,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(binding)
    }

}