package com.walisport.module.topup.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.RechargeMethodBean
import com.walisport.module.topup.databinding.ItemRechargeMethodBinding
import com.walisport.module.topup.ui.adapter.compare.RechargeMethodCompare

class WithdrawTypeAdapter(private val listener: PaTypeListener) :
    BaseAdapter<RechargeMethodBean, BaseViewHolder, ItemRechargeMethodBinding>(
        RechargeMethodCompare()
    ) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemRechargeMethodBinding,
        position: Int
    ) {
        val bean = getItem(position)
        binding.tvPayName.text = bean.payType
        binding.ivPayIcon.background = getPayIcon(bean.id)
        binding.tvPayRecommend.isVisible = bean.isRecommend
        binding.root.isSelected = bean.isSelect
        binding.root.setOnClickListener {
            listener.onSelectPayType(bean.id)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemRechargeMethodBinding {
        return ItemRechargeMethodBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemRechargeMethodBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    private fun getPayIcon(id: Int): Drawable {
        return when (id) {
            0 -> R.drawable.icon_type_ee.getDrawable()
            1 -> R.drawable.icon_type_bank.getDrawable()
            else -> R.drawable.icon_type_bank.getDrawable()
        }
    }

    interface PaTypeListener {
        fun onSelectPayType(id: Int)
    }
}