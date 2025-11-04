package com.walisport.module.live.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import com.walisport.module.live.data.model.LiveShareBean
import com.walisport.module.live.databinding.ItemLiveShareItemBinding

class LiveShareViewHolder(private val mBinding: ItemLiveShareItemBinding) :
    BaseViewHolder(mBinding) {

    fun init(bean: LiveShareBean) {
        mBinding.ivIcon.setImageResource(bean.icon)
        mBinding.tvTitle.text = bean.name
        mBinding.root.addScaleOnTouchAnimation()
        mBinding.root.setOnClickListener {
            bean.clickListener.invoke()
        }

    }

}