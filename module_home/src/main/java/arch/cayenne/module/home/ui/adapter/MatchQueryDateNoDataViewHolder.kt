package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.home.data.model.MatchQueryDateNoData
import arch.cayenne.module.home.databinding.ItemQueryDateNoDataBinding

class MatchQueryDateNoDataViewHolder(
    private val mBinding: ItemQueryDateNoDataBinding,
) : BaseViewHolder(mBinding) {

    init {
        mBinding.llContent.addScaleOnTouchAnimation()
    }

    @SuppressLint("SetTextI18n")
    fun bind(data: MatchQueryDateNoData, clickListener: () -> Unit) {
        with(mBinding) {
            tvLeagueName.text = data.leagueName
            llContent.clickNoRepeat { clickListener.invoke() }
        }
    }


}