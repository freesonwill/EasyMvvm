package com.walisport.module.topup.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.PayMethodBean
import com.walisport.module.topup.databinding.ItemPayMethodBinding
import com.walisport.module.topup.ui.adapter.compare.PayMethodCompare

class PayMethodAdapter(private val listener: PaTypeListener) :
    BaseAdapter<PayMethodBean, BaseViewHolder, ItemPayMethodBinding>(
        PayMethodCompare()
    ) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemPayMethodBinding, position: Int) {
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
    ): ItemPayMethodBinding {
        return ItemPayMethodBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemPayMethodBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    private fun getPayIcon(id: Int): Drawable {
        return when (id) {
            0 -> R.drawable.icon_type_zfb.getDrawable()
            1 -> R.drawable.icon_type_wx.getDrawable()
            2 -> R.drawable.icon_type_ee.getDrawable()
            3 -> R.drawable.icon_type_bank.getDrawable()
            4 -> R.drawable.icon_type_rg.getDrawable()
            5 -> R.drawable.icon_type_yun.getDrawable()
            6 -> R.drawable.icon_type_jd.getDrawable()
            7 -> R.drawable.icon_type_yl.getDrawable()
            else -> R.drawable.icon_type_yl.getDrawable()
        }
    }

    interface PaTypeListener {
        fun onSelectPayType(id: Int)
    }
}