package com.walisport.module.topup.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.RechargeRecordBean
import com.walisport.module.topup.databinding.ItemRechargeRecordBinding
import com.walisport.module.topup.ui.adapter.compare.TopupRecordItemCompare

class TopupRecordItemAdapter(private val onMatchItemClickListener: OnMatchItemClickListener? = null) :
    BaseAdapter<RechargeRecordBean, RechargeRecordItemViewHolder, ItemRechargeRecordBinding>(
        TopupRecordItemCompare()
    ) {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var showNoMoreData: Boolean = false

    override fun convertPlus(
        holder: RechargeRecordItemViewHolder,
        binding: ItemRechargeRecordBinding,
        position: Int
    ) {
        val item = getItem(position)
        holder.init(item)
        binding.root.setOnClickListener {
            onMatchItemClickListener?.onLiveEntryClick(getItem(holder.adapterPosition))
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemRechargeRecordBinding {
        return ItemRechargeRecordBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemRechargeRecordBinding,
        viewType: Int
    ): RechargeRecordItemViewHolder {
        return RechargeRecordItemViewHolder(binding, onMatchItemClickListener, viewPool)
    }

    override fun onBindViewHolder(
        holder: RechargeRecordItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        super.onBindViewHolder(holder, position, payloads)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun showNoMoreData(hasNoMore: Boolean) {
        showNoMoreData = hasNoMore
        notifyDataSetChanged()
    }
}

interface OnMatchItemClickListener {
    fun onLiveEntryClick(item: RechargeRecordBean)
}