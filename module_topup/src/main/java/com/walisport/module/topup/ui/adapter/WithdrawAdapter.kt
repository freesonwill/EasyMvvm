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

class WithdrawAdapter :
    BaseAdapter<RechargeRecordBean, BaseViewHolder, ItemWithdrawRecordBinding>(
        TopupRecordItemCompare()
    ) {

    private var listener: OnWithdrawItemClickListener? = null

    companion object {
        const val TYPE_SUC = 1     //提现成功
        const val TYPE_FAD = 2     //提现失败

        const val TYPE_BANK = 1    //银行卡提现
        const val TYPE_USDT = 2    //USDT提现
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemWithdrawRecordBinding,
        position: Int
    ) {
        val item = getItem(position)
        when (position) {
            0 -> {
                binding.itemRoot.background = SkinnableResourceManager.getDrawable(
                    binding.root.context,
                    R.drawable.bg_item_list_top
                )
            }

            itemCount - 1 -> {
                binding.viewLine.visibility = View.INVISIBLE
                binding.itemRoot.background = SkinnableResourceManager.getDrawable(
                    binding.root.context,
                    R.drawable.bg_item_list_bottom
                )
            }

            else -> {
                binding.itemRoot.background = SkinnableResourceManager.getDrawable(
                    binding.root.context,
                    R.drawable.bg_item_list_rect
                )
            }
        }
        binding.tvStatus.text = getStatus(item.status)
        binding.tvStatus.background = getBackground(item.status)
        binding.tvStatus.setTextColor(getStatusTextColor(item.status))
        binding.tvType.text = getPayMethod(item.type)
        binding.ivIcon.background = getPayIcon(item.type)
        binding.tvTime.text = DateUtils.getDisplayStr(item.time, "yyyy/MM/dd HH:mm")
        binding.tvAmount.text = item.amount
        binding.itemRoot.setOnClickListener {
            listener?.onClick(item)
        }
    }

    private fun getStatus(status: Int): String {
        return when (status) {
            TYPE_SUC -> R.string.tx_suc.getString()
            TYPE_FAD -> R.string.tx_fad.getString()
            else -> R.string.tx_shen.getString()
        }
    }

    private fun getStatusTextColor(status: Int): Int {
        return when (status) {
            TYPE_SUC -> R.color.pay_success.getColor()
            TYPE_FAD -> R.color.pay_failure.getColor()
            else -> R.color.pay_un_confirm.getColor()
        }
    }

    private fun getBackground(type: Int): Drawable {
        return when (type) {
            TYPE_SUC -> R.drawable.bg_status_tx_suc.getDrawable()
            TYPE_FAD -> R.drawable.bg_status_tx_fad.getDrawable()
            else -> R.drawable.bg_status_tx_sh.getDrawable()
        }
    }

    private fun getPayMethod(type: Int): String {
        return when (type) {
            TYPE_BANK -> R.string.tx_union.getString()
            TYPE_USDT -> R.string.tx_usdt.getString()
            else -> R.string.tx_union.getString()
        }
    }

    private fun getPayIcon(type: Int): Drawable {
        return when (type) {
            TYPE_BANK -> R.drawable.icon_pay_union.getDrawable()
            TYPE_USDT -> R.drawable.icon_pay_usdt.getDrawable()
            else -> R.drawable.icon_pay_ee.getDrawable()
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

    fun setOnItemClick(listener: OnWithdrawItemClickListener){
        this.listener = listener
    }

    interface OnWithdrawItemClickListener {
        fun onClick(item: RechargeRecordBean)
    }
}