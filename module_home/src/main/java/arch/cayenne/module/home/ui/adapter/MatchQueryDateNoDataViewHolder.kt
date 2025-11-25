package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.model.MatchDateItem
import arch.cayenne.module.home.data.model.MatchQueryDateNoData
import arch.cayenne.module.home.databinding.ItemMatchDateBinding
import arch.cayenne.module.home.databinding.ItemQueryDateNoDataBinding
import arch.cayenne.module.home.utils.DateUtils

class MatchQueryDateNoDataViewHolder(
    private val mBinding: ItemQueryDateNoDataBinding,
) : BaseViewHolder(mBinding) {

    @SuppressLint("SetTextI18n")
    fun init(data: MatchQueryDateNoData) {
        with(mBinding) {
            tvDate.text = data.timeStamp.toString()
        }
    }


}