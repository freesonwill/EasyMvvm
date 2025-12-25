package com.walisport.module.gamedetail.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import arch.cayenne.lib.common.R as RC
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.data.model.CurrencyInfoBean
import com.walisport.module.gamedetail.databinding.ItemCurrencySelectorBinding
import com.walisport.module.gamedetail.ui.compare.CurrencySelectorCompare

class CurrencySelectorAdapter :
    BaseAdapter<CurrencyInfoBean , BaseViewHolder, ViewBinding>(CurrencySelectorCompare()) {

    private var currencyId: Int = -1
    private var onCurrencySelectedListener: ((currencyId: Int) -> Unit)? = null

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val mBinding = binding as ItemCurrencySelectorBinding
        val mData = getItem(position)

        with(mBinding) {
            if(mData.id == -1) {
                Glide.with(root.context).load(R.drawable.ic_current_crypto).into(ivIcon)
                tvName.text = mData.name
                tvSymbol.visibility = View.GONE
                ivCheck.visibility = if (mData.id == currencyId) View.VISIBLE else View.GONE
            } else {
                Glide.with(root.context).load(R.drawable.ic_current_crypto).into(ivIcon)
                tvName.text = mData.name
                tvSymbol.apply {
                    mData.unit?.let { text = it }
                    visibility = if (mData.isVirtual) View.GONE else View.VISIBLE
                }
            }
            ivCheck.visibility = if (mData.id == currencyId) View.VISIBLE else View.GONE
            clRoot.setBackgroundColor(
                if (mData.id == currencyId) root.context.getColor(RC.color.color_003A42)
                else root.context.getColor(android.R.color.transparent)
            )
            root.clickNoRepeat {
                setSelected(mData.id)
                onCurrencySelectedListener?.invoke(mData.id)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return ItemCurrencySelectorBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelected(currencyId: Int) {
        this.currencyId = currencyId
        notifyDataSetChanged()
    }

    fun setOnCurrencySelectListener(onCurrencySelectedListener: ((currencyId: Int) -> Unit)?) {
        this.onCurrencySelectedListener = onCurrencySelectedListener
    }
}