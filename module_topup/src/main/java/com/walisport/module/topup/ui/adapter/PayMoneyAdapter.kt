package com.walisport.module.topup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.topup.data.entity.PayMoneyBean
import com.walisport.module.topup.databinding.ItemPayMoneyBinding
import com.walisport.module.topup.ui.adapter.compare.PayMoneyCompare

class PayMoneyAdapter(private val listener: PayMoneyListener) :
    BaseAdapter<PayMoneyBean, BaseViewHolder, ItemPayMoneyBinding>(
        PayMoneyCompare()
    ) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemPayMoneyBinding, position: Int) {
        val bean = getItem(position)
        binding.tvPayName.text = bean.payMoney
        binding.tvPayName.isVisible = !bean.isCustom
        binding.layEdit.isVisible = bean.isCustom
        binding.root.isSelected = bean.isSelect
        binding.root.setOnClickListener {
            listener.onSelectPayMoney(bean.id)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemPayMoneyBinding {
        return ItemPayMoneyBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemPayMoneyBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    interface PayMoneyListener {
        fun onSelectPayMoney(id: Int)
    }
}