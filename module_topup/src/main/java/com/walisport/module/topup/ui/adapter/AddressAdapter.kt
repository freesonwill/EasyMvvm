package com.walisport.module.topup.ui.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.AddressBean
import com.walisport.module.topup.databinding.ItemAddressBinding
import com.walisport.module.topup.ui.adapter.BankCardAdapter.OnItemClickListener
import com.walisport.module.topup.ui.adapter.compare.AddressCompare

class AddressAdapter : BaseAdapter<AddressBean, BaseViewHolder, ItemAddressBinding>(
    AddressCompare()
) {
    private var clicklistener: OnItemClickListener? = null

    @SuppressLint("SetTextI18n")
    override fun convertPlus(
        holder: BaseViewHolder, binding: ItemAddressBinding, position: Int
    ) {
        val item = getItem(position)
        binding.tvAddress.text = item.address
        binding.tvAddressTitle.text = item.coinType + item.remark
        binding.tvAddressType.text = item.addressType
        binding.viewLine.isVisible = position != itemCount - 1
        binding.ivAddressLogo.background = getCoinIcon(item.coinType)
        binding.root.setOnClickListener{
            clicklistener?.onItemClick(item)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemAddressBinding {
        return ItemAddressBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemAddressBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    private fun getCoinIcon(id: String): Drawable {
        return when (id) {
            "USDT" -> R.drawable.icon_pay_usdt.getDrawable()
            else -> R.drawable.icon_pay_usdt.getDrawable()
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clicklistener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bean: AddressBean)
    }
}