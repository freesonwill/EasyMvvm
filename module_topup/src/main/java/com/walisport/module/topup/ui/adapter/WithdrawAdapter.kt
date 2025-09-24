package com.walisport.module.topup.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.RechargeRecordBean
import com.walisport.module.topup.databinding.ItemWithdrawRecordBinding
import com.walisport.module.topup.ui.adapter.compare.TopupRecordItemCompare

class WithdrawAdapter(private val listener: OnWithdrawItemClickListener? = null) :
    BaseAdapter<RechargeRecordBean, BaseViewHolder, ItemWithdrawRecordBinding>(
        TopupRecordItemCompare()
    ) {

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemWithdrawRecordBinding,
        position: Int
    ) {
        val item = getItem(position)
        when (position) {
            0 -> {
                binding.itemRoot.background = SkinnableResourceManager.getDrawable(binding.root.context, R.drawable.bg_item_list_top)
            }
            itemCount - 1 -> {
                binding.viewLine.visibility = View.INVISIBLE
                binding.itemRoot.background = SkinnableResourceManager.getDrawable(binding.root.context, R.drawable.bg_item_list_bottom)
            }
            else -> {
                binding.itemRoot.background = SkinnableResourceManager.getDrawable(binding.root.context, R.drawable.bg_item_list_rect)
            }
        }
        binding.tvStatus.text = getStatus(item.status)
        binding.tvStatus.background = getBackground(item.status)
        binding.tvStatus.setTextColor(getStatusTextColor(item.status))
        binding.tvType.text = getPayMethod(item.type)
        binding.ivIcon.background = getIcon(item.type)
        binding.tvTime.text = DateUtils.getDisplayStr(item.time, "yyyy/MM/dd HH:mm")
        binding.tvAmount.text = item.amount
    }

    private fun getIcon(type: Int): Drawable {
        return when (type) {
            1 -> R.drawable.icon_pay_union.getDrawable()
            2 -> R.drawable.icon_pay_usdt.getDrawable()
            else -> R.drawable.icon_pay_union.getDrawable()
        }
    }

    private fun getStatus(status: Int): String {
        return when (status) {
            1 -> R.string.tx_suc.getString()
            2 -> R.string.tx_fad.getString()
            else -> R.string.tx_shen.getString()
        }
    }

    private fun getStatusTextColor(status: Int): Int {
        return when (status) {
            1 -> R.color.tx_success.getColor()
            2 -> R.color.pay_failure.getColor()
            else -> R.color.pay_un_confirm.getColor()
        }
    }

    private fun getBackground(type: Int): Drawable {
        return when (type) {
            1 -> R.drawable.bg_status_tx_suc.getDrawable()
            2 -> R.drawable.bg_status_tx_fad.getDrawable()
            else -> R.drawable.bg_status_tx_sh.getDrawable()
        }
    }

    private fun getPayMethod(type: Int): String {
        return when (type) {
            1 -> R.string.tx_union.getString()
            2 -> R.string.tx_usdt.getString()
            else -> R.string.tx_union.getString()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemWithdrawRecordBinding {
        return ItemWithdrawRecordBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemWithdrawRecordBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    interface OnWithdrawItemClickListener {
        fun onEntryClick(item: RechargeRecordBean)
    }
}