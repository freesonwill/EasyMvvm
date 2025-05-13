package com.walisport.module.live.ui.adapter.livebetslip.item

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.skin.widget.SkinnableView
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.LiveBetSlipItemManagerInterface
import com.walisport.module.live.utils.RecyclerItemListener

abstract class LiveBetSlipBaseItemManager(
    private val binding: ViewBinding,
    private val liveBetSlip: LiveBetSlipEnum
) : LiveBetSlipItemManagerInterface {
    var expandedListener: RecyclerItemListener<LiveBetSlipExpandedEnum>? = null

    companion object {

        fun initManager(
            binding: ViewBinding,
            liveBetSlip: LiveBetSlipEnum
        ): LiveBetSlipBaseItemManager? {
            return when (binding) {
                is ItemLiveBetSlipUnsettleBinding -> LiveBetSlipUnsettledItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipConfirmBinding -> LiveBetSlipConfirmItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipSettledBinding -> LiveBetSlipSettledItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipReserveBinding -> LiveBetSlipReserveItemManager(
                    binding,
                    liveBetSlip
                )

                is ItemLiveBetSlipInvalidBinding -> LiveBetSlipInvalidItemManager(
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
        expandedEnum: LiveBetSlipExpandedEnum? = null,
        line: SkinnableView,
        group: Group,
        tvMore: TextView,
        ivArrow: ImageView,
        position: Int,
        count: Int,
        llMore: LinearLayout
    ) {
        line.isVisible = position != count - 1
        group.isVisible = (count - 1) == position && expandedEnum != LiveBetSlipExpandedEnum.Hide
        llMore.tag = position
        if (expandedEnum != LiveBetSlipExpandedEnum.Hide) {
            tvMore.text = ContextCompat.getString(
                tvMore.context,
                if (expandedEnum == LiveBetSlipExpandedEnum.Fold) R.string.see_more else R.string.fold_up
            )
            ivArrow.setImageResource(if (expandedEnum == LiveBetSlipExpandedEnum.Fold) R.drawable.icon_cricle_arrrow_down else R.drawable.icon_cricle_arrrow_up)
        }
    }


}