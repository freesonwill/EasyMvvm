package com.walisport.module.topup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.walisport.module.topup.data.entity.CoinBean
import com.walisport.module.topup.databinding.ItemCoinBinding
import com.walisport.module.topup.ui.adapter.compare.CoinCompare

class CoinAdapter : BaseAdapter<CoinBean, BaseViewHolder, ItemCoinBinding>(
    CoinCompare()
) {
    private var clicklistener: OnItemClickListener? = null

    override fun convertPlus(holder: BaseViewHolder, binding: ItemCoinBinding, position: Int) {
        val item = getItem(position)
        binding.tvNameCoin.text = item.coinName
        binding.ivLogoCoin.background = item.coinLogo.getDrawable()
        binding.ivSelCoin.isVisible = item.isSelect
        binding.root.isSelected = item.isSelect
        binding.root.setOnClickListener {
            clicklistener?.onItemClick(item.id)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemCoinBinding {
        return ItemCoinBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemCoinBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clicklistener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }
}