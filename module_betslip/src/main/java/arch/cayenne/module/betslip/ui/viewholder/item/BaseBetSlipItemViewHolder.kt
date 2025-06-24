package arch.cayenne.module.betslip.ui.viewholder.item

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.utisl.BetSlipItemViewHolderInterface

abstract class BaseBetSlipItemViewHolder<VB: ViewBinding>(binding: ViewBinding): BaseViewHolder(binding),
    BetSlipItemViewHolderInterface {
    protected val mBinding: VB get() = binding as VB

    private var liveListener: BetSlipAdapter.BetSlipLiveListener? = null

    fun setLiveListener(listener: BetSlipAdapter.BetSlipLiveListener?) {
        liveListener = listener
    }

    protected fun showLiveArrow(data: BetSlipSelectionData, ivArrow: ImageView) {
        liveListener?.let {
            ivArrow.isVisible = it.isShowLiveButton()
            ivArrow.setOnClickListener { _ ->
                it.onLiveButtonClick(data)
            }
        }
    }

    abstract fun hideLastLine(isLast:Boolean)

    protected fun whenScoreIsNull(score: String): String {
        if (score.isEmpty()) {
            return "0-0"
        }
        return score
    }

}