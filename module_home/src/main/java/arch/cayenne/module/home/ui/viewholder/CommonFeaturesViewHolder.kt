package arch.cayenne.module.home.ui.viewholder

import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.module.home.data.model.CommonFeaturesBean
import arch.cayenne.module.home.databinding.ItemDrawerFeaturesBinding

class CommonFeaturesViewHolder(private val mBinding: ItemDrawerFeaturesBinding) :
    BaseViewHolder(mBinding) {

    fun init(bean: CommonFeaturesBean) {
        mBinding.ivIcon.setImageResource(bean.drawableId)
        mBinding.tvTitle.setTextRes(bean.titleResId)
        if (bean.isShowRectangleText) {
            mBinding.root.post {
                val viewGroup = mBinding.tvBadge.parent.parent as ViewGroup
                viewGroup.clipChildren = false
            }

            mBinding.tvBadge.visibility = View.VISIBLE
            mBinding.tvBadge.text = bean.rectangleText
        }
        mBinding.root.addScaleOnTouchAnimation()
        mBinding.root.setOnClickListener {
            bean.clickListener.invoke()
        }

    }

}