package arch.cayenne.module.home.ui.viewholder

import android.widget.TextView
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.getAwayScore
import arch.cayenne.lib.common.utils.ext.getHomeScore
import arch.cayenne.lib.common.utils.ext.toLocalDateTimeString
import arch.cayenne.lib.common.utils.ext.toMinuteSecondFormat
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
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

            // TODO 重構為動態生成賠率cell欄位, 串接投注點擊事件
            data.markets.let {
                tvWinTitle.text = it.getOrNull(0)?.market?.marketName.orEmpty()
                tvHandicapTitle.text = it.getOrNull(1)?.market?.marketName.orEmpty()
                tvOverTitle.text = it.getOrNull(2)?.market?.marketName.orEmpty()
                // 獨贏、讓球、大小三種玩法
                val winSelections =
                    it.getOrNull(0)?.selections
                val handicapSelections =
                    it.getOrNull(1)?.selections
                val overUnderSelections =
                    it.getOrNull(2)?.selections
                // 獨贏
                bindMarketOddsCell(winSelections?.getOrNull(0), tvHomeWinTitle, tvHomeWinOdds)  // 主
                bindMarketOddsCell(winSelections?.getOrNull(1), tvDrawTitle, tvDrawOdds)        // 和
                bindMarketOddsCell(winSelections?.getOrNull(2), tvAwayWinTitle, tvAwayWinOdds)  // 客

                // 讓球
                bindMarketOddsCell(
                    handicapSelections?.getOrNull(0),
                    tvHandicapHomeTitle,
                    tvHandicapHomeOdds
                )
                bindMarketOddsCell(
                    handicapSelections?.getOrNull(1),
                    tvHandicapAwayTitle,
                    tvHandicapAwayOdds
                )

                // 大小
                bindMarketOddsCell(overUnderSelections?.getOrNull(0), tvOverOddsTitle, tvOverOdds)
                bindMarketOddsCell(overUnderSelections?.getOrNull(1), tvUnderOddsTitle, tvUnderOdds)
            }

            tvWatchCount.text = liveInfo.viewerCount.toString()
        }

    }

    private fun bindMarketOddsCell(
        selection: SelectionBean?,
        titleView: TextView,
        oddsView: TextView
    ) {
        titleView.text = selection?.shortName.orEmpty()
        oddsView.text = selection?.odds.orEmpty()
    }
}