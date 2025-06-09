package com.walisport.module.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.MatchStatusEnum
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.constants.SearchResultRaceItemType
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.databinding.ItemSearchResultGridHeaderBinding
import com.walisport.module.search.databinding.ItemSearchResultGridItemBinding
import com.walisport.module.search.databinding.ItemSearchResultGridMoreBinding
import com.walisport.module.search.databinding.ItemSearchResultRaceBinding
import com.walisport.module.search.databinding.ItemSearchResultRaceHeaderBinding
import com.walisport.module.search.ui.compare.SearchResultGridCompare
import com.walisport.module.search.ui.compare.SearchResultRaceCompare
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchResultRaceAdapter: BaseAdapter<SearchResultRaceItemType, BaseViewHolder, ViewBinding>(
    SearchResultRaceCompare()
) {
    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    var onBetClick: ((SearchMatchBean) -> Unit)? = null
    var onFavoriteClick: ((SearchMatchBean) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchResultRaceItemType.Header -> VIEW_TYPE_HEADER
            is SearchResultRaceItemType.Item -> VIEW_TYPE_ITEM
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> {
                val headerBinding = binding as ItemSearchResultRaceHeaderBinding
                val item = getItem(position) as SearchResultRaceItemType.Header

                headerBinding.tvTitle.text =
                    run {
                        SimpleDateFormat(
                            ContextCompat.getString(
                                holder.itemView.context,
                                R.string.search_result_race_date_format_display
                            ),
                            Locale.getDefault()
                        )
                    }.run {
                        SimpleDateFormat("yyyy/M/d", Locale.getDefault()).parse(item.title)
                            ?.let { format(it) } ?: item.title
                    }
            }
            VIEW_TYPE_ITEM -> {
                val itemBinding = binding as ItemSearchResultRaceBinding
                val itemData = (getItem(position) as SearchResultRaceItemType.Item).data

                with(itemBinding) {
                    with(itemData) {
                        tvTitle.text = basicInfo.matchName
                        tvTime.text = run {
                            when (basicInfo.status) {
                                MatchStatusEnum.ONGOING ->
                                    holder.itemView.context.getString(R.string.search_result_race_playing)

                                else -> {
                                    SimpleDateFormat("HH:mm", Locale.getDefault())
                                        .format(Date(basicInfo.startTime))
                                }
                            }
                        }
                        Glide.with(holder.itemView.context)
                            .load(basicInfo.homeTeamIcon)
                            .placeholder(
                                SkinnableResourceManager.getDrawable(
                                    holder.itemView.context,
                                    R.drawable.ic_search_result_placeholder
                                )
                            )
                            .into(ivIconHomeTeam)

                        Glide.with(holder.itemView.context)
                            .load(basicInfo.awayTeamIcon)
                            .placeholder(
                                SkinnableResourceManager.getDrawable(
                                    holder.itemView.context,
                                    R.drawable.ic_search_result_placeholder
                                )
                            )
                            .into(ivIconAwayTeam)
                        tvNameHomeTeam.text = basicInfo.homeTeam
                        tvNameAwayTeam.text = basicInfo.awayTeam
                        listOf(
                            Pair(tvScoreHomeTeam, "${basicInfo.liveInfo?.homeScore ?: "-"}"),
                            Pair(tvScoreAwayTeam, "${basicInfo.liveInfo?.awayScore ?: "-"}")
                        ).forEach { (textView, score) ->
                            textView.text = score
                            textView.setTextColor(
                                SkinnableResourceManager.getColor(
                                    textView.context,
                                    if (score == "-") R.color.search_result_race_no_score
                                    else R.color.search_result_race_team_name
                                )
                            )
                        }
                        if (basicInfo.status == MatchStatusEnum.ENDED) {
                            val homeScore = basicInfo.liveInfo?.homeScore ?: 0
                            val awayScore = basicInfo.liveInfo?.awayScore ?: 0
                            if (homeScore > awayScore) {
                                ivArrowTeamHome.visibility = View.VISIBLE
                                ivArrowTeamAway.visibility = View.GONE
                            } else if (homeScore < awayScore) {
                                ivArrowTeamHome.visibility = View.GONE
                                ivArrowTeamAway.visibility = View.VISIBLE
                            } else {
                                ivArrowTeamHome.visibility = View.GONE
                                ivArrowTeamAway.visibility = View.GONE
                            }
                        }
                        btnFavorite.apply {
                            isSelected = collect
                            clickNoRepeat {
                                onFavoriteClick?.invoke(itemData)
                                isSelected = !isSelected
                            }
                        }
                        btnBet.apply {
                            if(basicInfo.betStop) {
                                text = holder.itemView.context.getString(R.string.search_result_btn_bet_finish)
                                isEnabled = false
                            } else {
                                text = holder.itemView.context.getString(R.string.search_result_btn_bet)
                                isEnabled = true
                                clickNoRepeat {
                                    onBetClick?.invoke(itemData)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            VIEW_TYPE_HEADER -> ItemSearchResultRaceHeaderBinding.inflate(inflater, parent, false)
            VIEW_TYPE_ITEM -> ItemSearchResultRaceBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    fun updateFavoriteStatus(matchId: Long, collectStatus: Boolean) {
        val index = currentList.indexOfFirst {
            (it as? SearchResultRaceItemType.Item)?.data?.matchId == matchId
        }
        if (index < 0) return

        val oldItem = currentList[index] as SearchResultRaceItemType.Item
        if (oldItem.data.collect == collectStatus) return

        val updatedItem = oldItem.copy(data = oldItem.data.copy(collect = collectStatus))
        val newList = currentList.toMutableList().apply {
            this[index] = updatedItem
        }
        submitList(newList)
    }
}