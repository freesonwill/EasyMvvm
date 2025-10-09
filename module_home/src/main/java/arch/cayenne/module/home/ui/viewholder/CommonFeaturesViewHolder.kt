package arch.cayenne.module.home.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.module.home.data.model.CommonFeaturesBean
import arch.cayenne.module.home.databinding.ItemDrawerFeaturesBinding

class CommonFeaturesViewHolder(private val mBinding: ItemDrawerFeaturesBinding) :
    BaseViewHolder(mBinding) {

    fun init(bean: CommonFeaturesBean) {
        mBinding.ivIcon.setImageResource(bean.drawableId)
        mBinding.tvTitle.setTextRes(bean.titleResId)

        mBinding.root.addScaleOnTouchAnimation()
        mBinding.root.setOnClickListener {
            bean.clickListener.invoke()
        }

    }

}