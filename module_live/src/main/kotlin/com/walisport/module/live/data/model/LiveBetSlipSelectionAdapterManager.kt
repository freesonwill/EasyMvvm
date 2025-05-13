package com.walisport.module.live.data.model

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.skin.widget.SkinnableView
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.LiveBetSlipResultOrderStatusEnum
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.LiveDateUtil
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common

class LiveBetSlipSelectionAdapterManager(binding: ViewBinding, type: LiveBetSlipEnum) {

    private val METHOD_UPDATE = 1
    private val METHOD_LISTENER = 2
    private val betType = type
    private val binding = binding
    private var itemListener: RecyclerItemListener<LiveBetSlipSelectionData>? = null


    fun initListener(listener: RecyclerItemListener<LiveBetSlipSelectionData>) {
        this.itemListener = listener
        managerView(METHOD_LISTENER, -1, -1, null)
    }

    fun updateView(
        position: Int,
        count: Int,
        expandedEnum: LiveBetSlipExpandedEnum,
        item: LiveBetSlipSelectionData? = null
    ) {
        managerView(METHOD_UPDATE, position, count, expandedEnum, item)
    }

    private fun managerView(
        method: Int,
        position: Int,
        count: Int,
        expandedEnum: LiveBetSlipExpandedEnum? = null,
        selection: LiveBetSlipSelectionData? = null
    ) {
        when (binding) {
            is ItemLiveBetSlipUnsettleBinding -> {
                when (method) {
                    METHOD_UPDATE -> {
                        binding.also {
                            configView(
                                expandedEnum,
                                it.line,
                                it.groupGradient,
                                it.tvMore,
                                it.ivArrow,
                                position,
                                count,
                                it.llMore
                            )
                            selection?.selection?.let { item ->
                                updateData(
                                    item,
                                    it.betUnsettledIvBall,
                                    it.betUnsettledTvRace,
                                    it.betUnsettledTvIntroduce,
                                    it.betUnsettledTvAodds,
                                    it.betUnsettledTvStatus,
                                    it.betUnsettledTvScore,
                                    it.betUnsettledTvStart
                                )
                            }
                        }
                    }

                    METHOD_LISTENER -> {
                        initMoreListener(binding.llMore)
                    }

                    else -> {}
                }

            }

            is ItemLiveBetSlipConfirmBinding -> {

                when (method) {
                    METHOD_UPDATE -> {
                        binding.also {
                            configView(
                                expandedEnum,
                                it.line,
                                it.groupGradient,
                                it.tvMore,
                                it.ivArrow,
                                position,
                                count,
                                it.llMore
                            )
                            selection?.selection?.let { item ->
                                updateData(
                                    item,
                                    it.betConfirmIvBall,
                                    it.betConfirmTvRace,
                                    it.betConfirmTvIntroduce,
                                    it.betConfirmTvAodds,
                                    it.betConfirmTvStatus,
                                    it.betConfirmTvScore,
                                    it.betConfirmTvStart
                                )
                            }
                        }

                    }

                    METHOD_LISTENER -> {
                        initMoreListener(binding.llMore)
                    }

                    else -> {}
                }
            }

            is ItemLiveBetSlipSettledBinding -> {
                when (method) {
                    METHOD_UPDATE -> {

                        binding.also {
                            configView(
                                expandedEnum,
                                it.line,
                                it.groupGradient,
                                it.tvMore,
                                it.ivArrow,
                                position,
                                count,
                                it.llMore
                            )
                            selection?.selection?.let { item ->
                                updateData(
                                    item,
                                    it.betSettledIvBall,
                                    it.betSettledTvRace,
                                    it.betSettledTvIntroduce,
                                    it.betSettledTvAodds,
                                    it.betSettledTvStatus,
                                    it.betSettledTvScore,
                                    tvScore1 = it.betSettledTvScore1
                                )
                                settledStatus(item, it)
                            }
                        }
                    }

                    METHOD_LISTENER -> {
                        initMoreListener(binding.llMore)
                    }

                    else -> {}
                }

            }

            is ItemLiveBetSlipReserveBinding -> {
                when (method) {
                    METHOD_UPDATE -> {
                        binding.also {
                            configView(
                                expandedEnum,
                                it.line,
                                it.groupGradient,
                                it.tvMore,
                                it.ivArrow,
                                position,
                                count,
                                it.llMore
                            )
                            selection?.reserve?.let { item ->
                                updateReserveData(item, binding)
                            }
                        }
                    }

                    METHOD_LISTENER -> {
                        initMoreListener(binding.llMore)
                    }

                    else -> {}
                }
            }

            is ItemLiveBetSlipInvalidBinding -> {

                when (method) {
                    METHOD_UPDATE -> {
                        binding.also {
                            configView(
                                expandedEnum,
                                it.line,
                                it.groupGradient,
                                it.tvMore,
                                it.ivArrow,
                                position,
                                count,
                                it.llMore
                            )
                            selection?.selection?.let { item ->
                                updateData(
                                    item,
                                    it.betInvalidIvBall,
                                    it.betInvalidTvRace,
                                    it.betInvalidTvIntroduce,
                                    it.betInvalidTvAodds,
                                    it.betInvalidTvMatchStatus,
                                    it.betInvalidTvScore,
                                    it.betInvalidTvStart
                                )
                            }
                        }
                    }

                    METHOD_LISTENER -> {
                        initMoreListener(binding.llMore)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initMoreListener(llMore: LinearLayout) {
        llMore.setOnClickListener {
            val position = llMore.tag as Int
            itemListener?.onItemClick(null, position)
        }
    }

    private fun configView(
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
        group.isVisible = expandedEnum != LiveBetSlipExpandedEnum.Hide
        llMore.tag = position
        if (expandedEnum != LiveBetSlipExpandedEnum.Hide) {
            tvMore.text = ContextCompat.getString(
                tvMore.context,
                if (expandedEnum == LiveBetSlipExpandedEnum.Fold) R.string.see_more else R.string.fold_up
            )
            ivArrow.setImageResource(if (expandedEnum == LiveBetSlipExpandedEnum.Fold) R.drawable.icon_cricle_arrrow_down else R.drawable.icon_cricle_arrrow_up)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData(
        item: Common.OrderSelection?,
        ivIcon: ImageView,
        tvMatch: TextView,
        tvSelection: TextView,
        tvOdds: TextView,
        tvStatus: TextView,
        tvScore: TextView,
        tvStart: TextView? = null,
        tvScore1: TextView? = null,
        isExpectedOdds: Boolean = false
    ) {
        item?.let {
            val match = item.matchBasic
            Glide.with(ivIcon.context).load(match.tournamentIcon).into(ivIcon)
            tvMatch.text = match.matchName
            tvSelection.text = item.selectionName
            tvOdds.text = if (isExpectedOdds) binding.root.resources.getString(
                R.string.live_bet_except_odds,
                "@${item.odds}"
            ) else "@${item.odds}"
//                TODO 滚球不清楚
            tvStatus.text = "滚球"
            tvScore.text = item.marketName + "  " + item.betScore
            tvStart?.text =
                LiveDateUtil.getMDHm(match.startTime)
            tvScore1?.text = item.endScore
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateReserveData(
        item: Common.ReserveOrderSelection,
        nBinding: ItemLiveBetSlipReserveBinding
    ) {
        val match = item.matchBasic
        Glide.with(nBinding.root.context).load(match.tournamentIcon).into(nBinding.betReserveIvBall)
        with(nBinding) {
            betReserveTvRace.text = match.matchName
            betReserveTvIntroduce.text = item.selectionName
            betReserveTvAodds.text =
                nBinding.root.context.getString(R.string.live_bet_except_odds, item.odds)
            betReserveTvScore.text = ContextCompat.getString(
                binding.root.context,
                R.string.live_bet_full_handicap
            ) + "  " + match.liveInfo.score
            betReserveTvStart.text =
                LiveDateUtil.getMDHm(match.startTime)
        }
    }

    private fun settledStatus(
        item: Common.OrderSelection,
        binding: ItemLiveBetSlipSettledBinding
    ) {
        binding.also {
            when (val status = LiveBetSlipResultOrderStatusEnum.getStatus(item.status)) {
                LiveBetSlipResultOrderStatusEnum.Win,
                LiveBetSlipResultOrderStatusEnum.Lose -> {
                    it.iv1.isVisible = true
                    it.betSettledTvStatus1.isVisible = false
                    val resId =
                        if (status == LiveBetSlipResultOrderStatusEnum.Win) R.drawable.icon_betslip_tick else R.drawable.icon_betslip_fork
                    it.iv1.setImageResource(resId)
                }

                LiveBetSlipResultOrderStatusEnum.WinHalf,
                LiveBetSlipResultOrderStatusEnum.UnSettled,
                LiveBetSlipResultOrderStatusEnum.Cancel,
                LiveBetSlipResultOrderStatusEnum.Tie,
                LiveBetSlipResultOrderStatusEnum.LoseHalf -> {
                    it.iv1.isVisible = false
                    it.betSettledTvStatus1.isVisible = true
                    it.betSettledTvStatus1.text =
                        ContextCompat.getString(it.betSettledTvStatus1.context, status.names)
                }

                else -> {}
            }
        }
    }

}