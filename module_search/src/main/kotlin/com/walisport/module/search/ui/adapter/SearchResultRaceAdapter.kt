package com.walisport.module.search.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.MatchStatusEnum
import com.walisport.module.search.data.constants.SearchResultRaceItemType
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.databinding.ItemSearchResultNoMoreBinding
import com.walisport.module.search.databinding.ItemSearchResultRaceBinding
import com.walisport.module.search.databinding.ItemSearchResultRaceHeaderBinding
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
        const val VIEW_TYPE_NO_MORE = 2
    }

    var onBetClick: ((SearchMatchBean) -> Unit)? = null
    var onFavoriteClick: ((SearchMatchBean) -> Unit)? = null

    private var locale: Locale = Locale.getDefault()
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchResultRaceItemType.Header -> VIEW_TYPE_HEADER
            is SearchResultRaceItemType.Item -> VIEW_TYPE_ITEM
            is SearchResultRaceItemType.NoMore -> VIEW_TYPE_NO_MORE
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> {
                val headerBinding = binding as ItemSearchResultRaceHeaderBinding
                val item = getItem(position) as SearchResultRaceItemType.Header

                headerBinding.tvTitle.apply {
                    updatePadding(
                        top = if (position == 0) 4.dp2px else paddingTop
                    )
                    text =
                        run {
                            SimpleDateFormat(
                                SkinnableResourceManager.getString(
                                    holder.itemView.context,
                                    R.string.search_result_race_date_format_display,
                                    locale
                                ),
                                locale
                            )
                        }.run {
                            SimpleDateFormat("yyyy/M/d", locale).parse(item.title)
                                ?.let { format(it) } ?: item.title
                        }
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
                                    SkinnableResourceManager.getString(
                                        holder.itemView.context,
                                        R.string.search_result_race_playing,
                                        locale
                                    )

                                else -> {
                                    SimpleDateFormat("HH:mm", locale)
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
                            ivArrowTeamHome.visibility = if (homeScore > awayScore) View.VISIBLE else View.GONE
                            ivArrowTeamAway.visibility = if (homeScore < awayScore) View.VISIBLE else View.GONE
                        } else {
                            ivArrowTeamHome.visibility = View.GONE
                            ivArrowTeamAway.visibility = View.GONE
                        }
                        btnFavorite.apply {
                            isEnabled = !basicInfo.betStop
                            isSelected = collect

                            val drawable = AppCompatResources.getDrawable(context, R.drawable.layer_search_result_favorite)?.mutate() as? LayerDrawable
                            setImageDrawable(drawable)

                            drawable?.let {
                                animateFavoriteIcon(it, isSelected, force = true)
                            }

                            clickNoRepeat {
                                onFavoriteClick?.invoke(itemData)
                                isSelected = !isSelected

                                drawable?.let {
                                    animateFavoriteIcon(it, isSelected, force = false)
                                }
                            }
                        }
                        btnBet.apply {
                            if(basicInfo.betStop) {
                                text =
                                    SkinnableResourceManager.getString(
                                        holder.itemView.context,
                                        R.string.search_result_btn_bet_finish,
                                        locale
                                    )
                                isEnabled = false
                            } else {
                                text =
                                    SkinnableResourceManager.getString(
                                        holder.itemView.context,
                                        R.string.search_result_btn_bet,
                                        locale
                                    )
                                isEnabled = true
                                clickNoRepeat {
                                    onBetClick?.invoke(itemData)
                                }
                            }
                        }
                    }
                }
            }
            VIEW_TYPE_NO_MORE -> Unit
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
            VIEW_TYPE_NO_MORE -> ItemSearchResultNoMoreBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        val holder = BaseViewHolder(binding)
        if (viewType == VIEW_TYPE_ITEM) {
            (binding as? ItemSearchResultRaceBinding)?.apply {
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    btnBet,
                    10,
                    14,
                    1,
                    TypedValue.COMPLEX_UNIT_SP
                )
            }
        }
        return holder
    }

    private fun animateFavoriteIcon(
        drawable: LayerDrawable,
        selected: Boolean,
        force: Boolean
    ) {
        val unselected = drawable.findDrawableByLayerId(R.id.unselect)
        val selectedDrawable = drawable.findDrawableByLayerId(R.id.selected)

        val fadeIn = ObjectAnimator.ofInt(
            selectedDrawable,
            "alpha",
            if (selected) 0 else 255,
            if (selected) 255 else 0
        )
        val fadeOut = ObjectAnimator.ofInt(
            unselected,
            "alpha",
            if (selected) 255 else 0,
            if (selected) 0 else 255
        )

        val duration = if (force) 0L else 200L
        fadeIn.duration = duration
        fadeOut.duration = duration

        fadeIn.start()
        fadeOut.start()
    }

    /**
     * 更新语言设置
     * @param locale 新的语言环境
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateLanguage(locale: Locale) {
        this.locale = locale
        notifyDataSetChanged()
    }

    /**
     * 更新比赛的收藏状态
     * @param matchId 比赛ID
     * @param collectStatus 收藏状态，true表示已收藏，false表示未收藏
     */
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

        recyclerView?.apply {
            // 避免更新時觸發動畫造成畫面閃爍
            val oriAnimator = itemAnimator
            itemAnimator = null
            submitList(newList) {
                post { itemAnimator = oriAnimator }
            }
        }
    }
}