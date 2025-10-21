package com.walisport.module.me.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import com.walisport.module.me.data.model.FeaturesBean
import com.walisport.module.me.databinding.ItemFeaturesBinding

class FeaturesViewHolder(private val mBinding: ItemFeaturesBinding) :
    BaseViewHolder(mBinding) {

    fun init(bean: FeaturesBean) {
        mBinding.ivIcon.setImageResource(bean.drawableId)
        mBinding.tvTitle.setTextRes(bean.titleResId)

        mBinding.root.addScaleOnTouchAnimation()
        mBinding.root.setOnClickListener {
            bean.clickListener.invoke()
        }

    }

}