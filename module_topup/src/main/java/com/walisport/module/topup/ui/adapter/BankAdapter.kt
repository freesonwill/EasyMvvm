package com.walisport.module.topup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.topup.data.entity.BankBean
import com.walisport.module.topup.databinding.ItemBankBodyBinding
import com.walisport.module.topup.databinding.ItemBankHeaderBinding
import com.walisport.module.topup.ui.adapter.compare.BankCompare

class BankAdapter : BaseAdapter<BankBean, BaseViewHolder, ViewBinding>(
    BankCompare()
) {
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_BODY = 1
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val item = getItem(position)
        if (binding is ItemBankBodyBinding) {
            binding.tvBankName.text = item.bankName
        } else if (binding is ItemBankHeaderBinding) {
            binding.tvHeaderName.text = item.bankLetter
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        if (viewType == TYPE_HEADER) {
            return ItemBankHeaderBinding.inflate(inflater, parent, false)
        }
        return ItemBankBodyBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isBankLetter(position)) TYPE_HEADER else TYPE_BODY
    }

    private fun isBankLetter(position: Int): Boolean {
        val item = getItem(position)
        return item.isHeader
    }
}