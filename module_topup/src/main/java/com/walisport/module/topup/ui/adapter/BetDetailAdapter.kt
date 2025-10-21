package com.walisport.module.topup.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.BetDetailBean
import com.walisport.module.topup.databinding.ItemBetDetailBinding
import com.walisport.module.topup.ui.adapter.compare.BetDetailCompare
import java.text.SimpleDateFormat
import java.util.Locale

class BetDetailAdapter : BaseAdapter<BetDetailBean, BaseViewHolder, ItemBetDetailBinding>(
    BetDetailCompare()
) {

    override fun convertPlus(holder: BaseViewHolder, binding: ItemBetDetailBinding, position: Int) {
        val bean = getItem(position)
        binding.tvBetType.text = getPayType(bean.payType)
        binding.ivBetDetail.background = getPayIcon(bean.coinType)
        binding.tvBetMoney.text = String.format("¥ %s", bean.payMoney)
        binding.tvBetRate.text = bean.betRate
        binding.tvBetTz.text = String.format("%s/%s", bean.betMoney, bean.payMoney)
        binding.tvBetTime.text = getTimeStamp(bean.timestamp)
        binding.betDetailLine.isVisible = position != itemCount - 1
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemBetDetailBinding {
        return ItemBetDetailBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemBetDetailBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    private fun getPayType(id: Int): String {
        return when (id) {
            0 -> R.string.bet_pay_bank.getString()
            1 -> R.string.bet_pay_alipay.getString()
            2 -> R.string.bet_pay_wechat.getString()
            else -> R.string.bet_pay_bank.getString()
        }
    }

    private fun getPayIcon(id: Int): Drawable {
        return when (id) {
            0 -> R.drawable.icon_bet_detail_rmb.getDrawable()
            else -> R.drawable.icon_bet_detail_rmb.getDrawable()
        }
    }

    private fun getTimeStamp(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(time)
    }
}