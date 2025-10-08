package com.walisport.module.topup.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.topup.data.entity.BankCardBean
import com.walisport.module.topup.databinding.ItemBankCardBinding
import com.walisport.module.topup.ui.adapter.compare.BankCardCompare

class BankCardAdapter : BaseAdapter<BankCardBean, BaseViewHolder, ItemBankCardBinding>(
    BankCardCompare()
) {
    private var isEdit: Boolean = false
    private var clicklistener: OnItemClickListener? = null

    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder, binding: ItemBankCardBinding, position: Int
    ) {
        val item = getItem(position)
        binding.tvBankName.text = item.bankName
        binding.layoutItem.background = getBgColor(item.bgColor)
        binding.tvBankNum.text = getBankMaskNumber(item.bankNum)
        if (isEdit) {
            binding.radioDel.visibility = View.VISIBLE
            binding.radioSelect.visibility = View.GONE
        } else {
            binding.radioDel.visibility = View.GONE
            binding.radioSelect.visibility = View.VISIBLE
            if (item.isSelected) {
                binding.radioSelect.visibility = View.VISIBLE
            } else {
                binding.radioSelect.visibility = View.GONE
            }
        }
        binding.radioDel.setOnClickListener {
            clicklistener?.onItemDelete(item.id, item.bankName, item.bankNum)
        }
        binding.root.setOnClickListener {
            clicklistener?.onItemClick(item.id)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEditStatus(status: Boolean) {
        isEdit = status
        notifyDataSetChanged()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemBankCardBinding {
        return ItemBankCardBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemBankCardBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clicklistener = listener
    }

    private fun getBgColor(color: String): GradientDrawable {
        val shape = GradientDrawable()
        if (color.isEmpty()) {
            shape.setColor(Color.parseColor("#B62939"))
        }
        shape.setColor(Color.parseColor(color))
        shape.cornerRadius = 9.dp2px.toFloat()
        return shape
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
        fun onItemDelete(id: Int, bank: String, num: String)
    }

    private fun getBankMaskNumber(bankNum: String): String {
        val length = bankNum.length
        if (length < 4) {
            return bankNum
        }
        return "**** " + bankNum.substring(length - 4, length)
    }
}