package com.walisport.module.live.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.skin.widget.SportView
import com.walisport.module.live.R
import com.walisport.module.live.compare.LiveBetSlipSelectionCompare
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.ItemLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.ItemLiveBetSlipUnsettleBinding
import com.walisport.module.live.utils.LiveDateUtil
import galaxy.common.proto.Common
import org.w3c.dom.Text

class LiveBetSlipSelectionAdapter(betSlipType: LiveBetSlipEnum) :
    BaseAdapter<Common.OrderSelection, LiveBetSlipSelectionAdapter.LiveBetSlipSelectionViewHolder, ViewBinding>(
        LiveBetSlipSelectionCompare()
    ) {
    private val betType = betSlipType
    private var gradient = false

    fun updateGradient(flag: Boolean) {
        this.gradient = flag
    }


    inner class LiveBetSlipSelectionViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {

        fun initView(item: Common.OrderSelection, position: Int, count: Int) {
            when (binding) {
                is ItemLiveBetSlipUnsettleBinding -> {
                    val nBinding = binding as ItemLiveBetSlipUnsettleBinding
                    configView(
                        nBinding.line, nBinding.groupGradient, position, count
                    )
                    nBinding.also {
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

                is ItemLiveBetSlipConfirmBinding -> {
                    val nBinding = binding as ItemLiveBetSlipConfirmBinding
                    configView(
                        nBinding.line, nBinding.groupGradient, position, count
                    )
                    nBinding.also {
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

                is ItemLiveBetSlipSettledBinding -> {
                    val nBinding = binding as ItemLiveBetSlipSettledBinding
                    configView(
                        nBinding.line, nBinding.groupGradient, position, count
                    )
                    nBinding.also {
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
                    }
                }

                is ItemLiveBetSlipReserveBinding -> {

                }

                is ItemLiveBetSlipInvalidBinding -> {
                    val nBinding = binding as ItemLiveBetSlipInvalidBinding
                    configView(
                        nBinding.line, nBinding.groupGradient, position, count
                    )
                    nBinding.also {
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
        }

        private fun configView(line: SportView, group: Group, position: Int, count: Int) {
            line.isVisible = position != count - 1
            group.isVisible = position == 2 && gradient
        }

        @SuppressLint("SetTextI18n")
        private fun updateData(
            item: Common.OrderSelection,
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
            //TODO icon没有数据
            // Glide.with(root.context).load(match.tournamentIcon).into(betUnsettledIvBall)
            val match = item.matchBasic
            val expectedOdds = if (isExpectedOdds) ContextCompat.getString(
                binding.root.context,
                R.string.live_bet_except_odds
            ) + "  " else ""

            tvMatch.text = match.matchName
            tvSelection.text = item.selectionName
            tvOdds.text = expectedOdds + "@" + item.odds
//                TODO 滚球不清楚
            tvStatus.text = "滚球"
            tvScore.text = ContextCompat.getString(
                binding.root.context,
                R.string.live_bet_full_handicap
            ) + "  " + item.betScore
            tvStart?.text =
                LiveDateUtil.getMDHm(match.startTime)
            tvScore1?.text = item.endScore
        }


    }

    override fun convertPlus(
        holder: LiveBetSlipSelectionViewHolder, binding: ViewBinding, position: Int
    ) {
        holder.initView(getItem(position), position, itemCount)
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betType) {
            LiveBetSlipEnum.UnSettled -> ItemLiveBetSlipUnsettleBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Confirming -> ItemLiveBetSlipConfirmBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Settled -> ItemLiveBetSlipSettledBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Reserve -> ItemLiveBetSlipReserveBinding.inflate(
                inflater, parent, false
            )

            LiveBetSlipEnum.Invalid -> ItemLiveBetSlipInvalidBinding.inflate(
                inflater, parent, false
            )
        }

    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): LiveBetSlipSelectionViewHolder {
        val holder = LiveBetSlipSelectionViewHolder(binding)
        return holder
    }


}