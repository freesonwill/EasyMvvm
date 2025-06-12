package arch.cayenne.module.betslip.ui.viewholder.item

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.skin.widget.SkinnableView
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.utisl.BetSlipItemManagerInterface
import org.koin.java.KoinJavaComponent

abstract class BaseBetSlipItemViewHolder<VB: ViewBinding>(binding: ViewBinding): BaseViewHolder(binding),
    BetSlipItemManagerInterface {
    protected val mBinding: VB get() = binding as VB

    private val userManager: UserDataManager by KoinJavaComponent.inject(UserDataManager::class.java)
    private var expandedListener: RecyclerItemListener<BetSlipExpandedEnum>? = null
    private var liveListener: RecyclerItemListener<BetSlipSelectionData>? = null

    fun setExpandedListener(listener: RecyclerItemListener<BetSlipExpandedEnum>?) {
        expandedListener = listener
    }

    fun setLiveListener(listener: RecyclerItemListener<BetSlipSelectionData>?) {
        liveListener = listener
    }

    protected fun configView(
        expandedEnum: BetSlipExpandedEnum? = null,
        line: SkinnableView,
        group: ConstraintLayout,
        tvMore: TextView,
        ivMoreArrow: ImageView,
        count: Int
    ) {
        val position = adapterPosition
        line.isVisible = position != count - 1
        group.isVisible = (count - 1) == position && expandedEnum != BetSlipExpandedEnum.Hide
        if (expandedEnum != BetSlipExpandedEnum.Hide) {
            tvMore.text = ContextCompat.getString(
                tvMore.context,
                if (expandedEnum == BetSlipExpandedEnum.Fold) R.string.see_more else R.string.fold_up
            )

            ivMoreArrow.setImageResource(if (expandedEnum == BetSlipExpandedEnum.Fold) R.drawable.icon_cricle_arrrow_down else R.drawable.icon_cricle_arrrow_up)
        }
    }

    protected fun initMoreListener(llMore: ViewGroup) {
        llMore.setOnClickListener {
            expandedListener?.onItemClick(null, adapterPosition)
        }
    }

    protected fun showLiveArrow(data: BetSlipSelectionData, ivArrow: ImageView) {
        val isDetail: Boolean = userManager.getValue(UserDataKey.KEY_BETSLIP_DETAIL, false)
        ivArrow.isVisible = isDetail
        ivArrow.setOnClickListener {
            liveListener?.onItemClick(data, adapterPosition)
        }
    }

}