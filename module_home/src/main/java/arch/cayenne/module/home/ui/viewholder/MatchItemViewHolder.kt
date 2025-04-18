package arch.cayenne.module.home.ui.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportStringExt.getAwayScore
import arch.cayenne.lib.common.utils.ext.SportStringExt.getHomeScore
import arch.cayenne.lib.common.utils.ext.toLocalDateTimeString
import arch.cayenne.lib.common.utils.ext.toMinuteSecondFormat
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.databinding.ItemOddsCellBinding
import com.bumptech.glide.Glide

class MatchItemViewHolder(private val mBinding: ItemMatchCardBinding) : BaseViewHolder(mBinding) {

    fun init(data: MatchWithMarkets) {
        with(mBinding) {
            val basicInfo = data.match.basicInfo
            val liveInfo = data.match.liveInfo
            Glide.with(binding.root).load(basicInfo.tournamentIcon).into(ivTournamentIcon)
            tvTournamentName.text = basicInfo.tournamentName
            //TODO 階段與時間待確認
            if (basicInfo.status == 4) {
                tvGameStatus.text = basicInfo.startTime.toLocalDateTimeString()
                tvGameTime.visibility = TextView.GONE
            } else {
                tvGameStatus.text = liveInfo.period
                tvGameTime.visibility = TextView.VISIBLE
                tvGameTime.text = liveInfo.clock.toMinuteSecondFormat()
            }

            Glide.with(binding.root).load(basicInfo.awayTeamIcon).into(ivAwayIcon)
            tvAwayName.text = basicInfo.awayTeam
            tvAwayScore.text = liveInfo.score.getAwayScore()

            Glide.with(binding.root).load(basicInfo.homeTeamIcon).into(ivHomeIcon)
            tvHomeName.text = basicInfo.homeTeam
            tvHomeScore.text = liveInfo.score.getHomeScore()

            val markets = data.markets
                .filter { it.selections.isNotEmpty() }

            val columnCount = markets.size
            layoutOddsTitle.columnCount = columnCount
            layoutOddsGrid.columnCount = columnCount

            markets.forEachIndexed { index, bean ->
                val titleView = TextView(binding.root.context).apply {
                    text = bean.market.marketName
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.secondary_title
                        )
                    )
                    textSize = 13f
                    setPadding(5, 4, 5, 4)
                }
                val lp = GridLayout.LayoutParams().apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(index, 1f)
                    setMargins(3.dp2px, 0, 0, 0)
                }

                layoutOddsTitle.addView(titleView, lp)
            }

            val maxRowCount = markets.maxOfOrNull { it.selections.size } ?: 0
            layoutOddsGrid.rowCount = maxRowCount

            for (rowIndex in 0 until maxRowCount) {
                markets.forEachIndexed { columnIndex, market ->
                    val selections = market.selections
                    val selection = selections.getOrNull(rowIndex)
                    "joseph row:$rowIndex column:$columnIndex selection:$selection".logd()

                    val view = LayoutInflater.from(binding.root.context)
                        .inflate(R.layout.item_odds_cell, layoutOddsGrid, false)
                    val oddsCellBinding = ItemOddsCellBinding.bind(view)

                    if (selection != null) {
                        oddsCellBinding.tvShortName.text = selection.shortName
                        oddsCellBinding.tvOdds.text = selection.odds

                        if (!selection.active) {
                            oddsCellBinding.tvShortName.visibility = View.GONE
                            oddsCellBinding.tvOdds.visibility = View.GONE
                            oddsCellBinding.ivLock.visibility = View.VISIBLE
                            oddsCellBinding.llOddsCell.isEnabled = false
                        } else {
                            oddsCellBinding.ivLock.visibility = View.GONE
                            oddsCellBinding.tvShortName.visibility = View.VISIBLE
                            oddsCellBinding.tvOdds.visibility = View.VISIBLE
                            oddsCellBinding.llOddsCell.isEnabled = true
                            oddsCellBinding.llOddsCell.setOnClickListener {
//                                onOddsClick?.invoke(selection)
                            }
                        }
                    } else {
                        // 無此 row 資料，顯示空白佔位
                        oddsCellBinding.tvShortName.visibility = View.INVISIBLE
                        oddsCellBinding.tvOdds.visibility = View.INVISIBLE
                        oddsCellBinding.ivLock.visibility = View.INVISIBLE
                        oddsCellBinding.llOddsCell.isEnabled = false
                    }

                    val cellParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 43.dp2px
                        columnSpec = GridLayout.spec(columnIndex, 1f)
                        rowSpec = GridLayout.spec(rowIndex, 1f)
                        setMargins(0, 2.dp2px, 2.dp2px, 0)
                    }

                    layoutOddsGrid.addView(oddsCellBinding.root, cellParams)
                }
            }
        }
    }
}