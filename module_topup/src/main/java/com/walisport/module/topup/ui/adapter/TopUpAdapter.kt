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
import com.walisport.module.topup.databinding.ItemRechargeRecordBinding
import com.walisport.module.topup.ui.adapter.compare.TopupRecordItemCompare

class TopUpAdapter : BaseAdapter<RechargeRecordBean, BaseViewHolder, ItemRechargeRecordBinding>(
    TopupRecordItemCompare()
) {

    private var listener: OnTopUpItemClickListener? = null

    companion object {
        const val TYPE_SUC = 1     //充值成功
        const val TYPE_FAD = 2     //充值失败

        const val TYPE_BANK = 1    //银行卡充值
        const val TYPE_ALI = 2     //支付宝充值
        const val TYPE_WECHAT = 3  //微信充值
        const val TYPE_YUN = 4     //云闪付充值
        const val TYPE_EE = 5      //EE钱包充值
        const val TYPE_RMB = 6     //数字人民币充值
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemRechargeRecordBinding,
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
            TYPE_SUC -> R.string.cz_suc.getString()
            TYPE_FAD -> R.string.cz_fad.getString()
            else -> R.string.cz_shen.getString()
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
            TYPE_BANK -> R.string.cz_bank.getString()
            TYPE_ALI -> R.string.cz_ali.getString()
            TYPE_WECHAT -> R.string.cz_wechat.getString()
            TYPE_YUN -> R.string.cz_yun.getString()
            TYPE_EE -> R.string.cz_ee.getString()
            TYPE_RMB -> R.string.cz_rmb.getString()
            else -> R.string.cz_bank.getString()
        }
    }

    private fun getPayIcon(type: Int): Drawable {
        return when (type) {
            TYPE_BANK -> R.drawable.icon_pay_union.getDrawable()
            TYPE_ALI -> R.drawable.icon_pay_ali.getDrawable()
            TYPE_WECHAT -> R.drawable.icon_pay_wechat.getDrawable()
            TYPE_YUN -> R.drawable.icon_pay_yun.getDrawable()
            TYPE_EE -> R.drawable.icon_pay_ee.getDrawable()
            TYPE_RMB -> R.drawable.icon_pay_rmb.getDrawable()
            else -> R.drawable.icon_pay_union.getDrawable()
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
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    fun setOnItemClick(listener: OnTopUpItemClickListener) {
        this.listener = listener
    }

    interface OnTopUpItemClickListener {
        fun onClick(item: RechargeRecordBean)
    }
}