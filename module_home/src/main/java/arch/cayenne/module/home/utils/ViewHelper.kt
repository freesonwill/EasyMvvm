package arch.cayenne.module.home.utils

import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import com.bumptech.glide.Glide

internal object ViewHelper {

    fun bindMatchItem(data: MatchWithMarkets, binding: ItemMatchCardBinding) {
        "joseph $data".logd()
        with(binding) {
            val basicInfo = data.match.basicInfo
            val liveInfo = data.match.liveInfo
            Glide.with(binding.root).load(basicInfo.tournamentIcon).into(ivTournamentIcon)
            tvTournamentName.text = basicInfo.tournamentName
            //TODO 階段與時間待確認
            tvGameStatus.text = liveInfo.period
            tvGameTime.text = liveInfo.clock.toString()

            Glide.with(binding.root).load(basicInfo.awayTeamIcon).into(ivAwayIcon)
            tvAwayName.text = basicInfo.awayTeam
            tvAwayScore.text = liveInfo.score

            Glide.with(binding.root).load(basicInfo.homeTeamIcon).into(ivHomeIcon)
            tvHomeName.text = basicInfo.homeTeam
            tvHomeScore.text = liveInfo.score

            // TODO 賠率待實作

            tvWatchCount.text = liveInfo.viewerCount.toString()
        }
    }
}