package arch.cayenne.module.betslip.ui.viewholder.item

import android.text.Spannable
import android.text.SpannableString
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.view.VerticalOffsetSpan
import arch.cayenne.module.betslip.utisl.BetSlipItemViewHolderInterface

abstract class BaseBetSlipItemViewHolder<VB: ViewBinding>(binding: ViewBinding): BaseViewHolder(binding),
    BetSlipItemViewHolderInterface {
    protected val mBinding: VB get() = binding as VB

    private var liveListener: BetSlipAdapter.BetSlipLiveListener? = null

    fun setLiveListener(listener: BetSlipAdapter.BetSlipLiveListener?) {
        liveListener = listener
    }

    protected fun showLiveArrow(status: Int, data: BetSlipSelectionData, ivArrow: ImageView) {
        liveListener?.let {
            ivArrow.isVisible = it.isShowLiveButton() && isShowLiveButton(status)
            ivArrow.setOnClickListener { _ ->
                it.onLiveButtonClick(data)
            }
        }
    }

    private fun isShowLiveButton(status: Int): Boolean {
        return status == 1 ||
                status == 4 ||
                status == 5 ||
                status == 6 ||
                status == 8
    }

    abstract fun hideLastLine(isLast:Boolean)

    protected fun whenScoreIsNull(score: String): String {
        if (score.isEmpty()) {
            return "0-0"
        }
        return score
    }

    /**
     * 预计赔率中@字符和数字与中文的对齐基线不一致
     * */

    protected fun expectOdds(str:String):SpannableString{
        val spannable = SpannableString(str)
        val atIndex = str.indexOf("@")
        if (atIndex != -1) {
            spannable.setSpan(
                VerticalOffsetSpan(-4),
                atIndex,
                atIndex+1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                VerticalOffsetSpan(-2),
                atIndex+1,
                str.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }

}