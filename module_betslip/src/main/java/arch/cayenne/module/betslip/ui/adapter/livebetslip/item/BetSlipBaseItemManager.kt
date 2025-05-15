package arch.cayenne.module.betslip.ui.adapter.livebetslip.item

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.skin.widget.SkinnableView
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.utisl.BetSlipItemManagerInterface
import arch.cayenne.module.betslip.utisl.RecyclerItemListener
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum


abstract class BetSlipBaseItemManager(
    private val binding: ViewBinding,
    private val liveBetSlip: BetSlipEnum
) : BetSlipItemManagerInterface {
    var expandedListener: RecyclerItemListener<BetSlipExpandedEnum>? = null

    companion object {

        fun initManager(
            binding: ViewBinding,
            liveBetSlip: arch.cayenne.module.betslip.data.constants.BetSlipEnum
        ): BetSlipBaseItemManager? {
            return when (binding) {
                is ItemLiveBetSlipUnsettleBinding -> BetSlipUnsettledItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipConfirmBinding -> BetSlipConfirmItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipSettledBinding -> BetSlipSettledItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipReserveBinding -> BetSlipReserveItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipInvalidBinding -> BetSlipInvalidItemManager(
                    binding,
                    liveBetSlip
                )

                else -> null
            }
        }
    }

    fun initMoreListener(llMore: LinearLayout) {
        llMore.setOnClickListener {
            val position = llMore.tag as Int
            expandedListener?.onItemClick(null, position)
        }
    }

    fun configView(
        expandedEnum: BetSlipExpandedEnum? = null,
        line: SkinnableView,
        group: ConstraintLayout,
        tvMore: TextView,
        ivArrow: ImageView,
        position: Int,
        count: Int,
        llMore: LinearLayout
    ) {
        line.isVisible = position != count - 1
        group.isVisible = (count - 1) == position && expandedEnum != BetSlipExpandedEnum.Hide
        llMore.tag = position
        if (expandedEnum != BetSlipExpandedEnum.Hide) {
            tvMore.text = ContextCompat.getString(
                tvMore.context,
                if (expandedEnum == BetSlipExpandedEnum.Fold) R.string.see_more else R.string.fold_up
            )

            ivArrow.setImageResource(if (expandedEnum == BetSlipExpandedEnum.Fold) R.drawable.icon_cricle_arrrow_down else R.drawable.icon_cricle_arrrow_up)
        }
    }


}