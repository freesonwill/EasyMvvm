package arch.cayenne.module.home.utils

import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.module.home.data.Match
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import com.bumptech.glide.Glide

internal object ViewHelper {

    fun bindMatchItem(data: Match, binding: ItemMatchCardBinding) {
        "joseph $data".logd()
        with(binding) {
            Glide.with(binding.root).load(data.basicInfo.tournamentIcon).into(ivTournamentIcon)
            tvTournamentName.text = data.basicInfo.tournamentName
            //TODO 階段與時間待確認
            tvGameStatus.text = data.basicInfo.liveInfo.period
            tvGameTime.text = data.basicInfo.liveInfo.clock.toString()

            Glide.with(binding.root).load(data.basicInfo.awayTeamIcon).into(ivAwayIcon)
            tvAwayName.text = data.basicInfo.awayTeam
            tvAwayScore.text = data.basicInfo.liveInfo.score

            Glide.with(binding.root).load(data.basicInfo.homeTeamIcon).into(ivHomeIcon)
            tvHomeName.text = data.basicInfo.homeTeam
            tvHomeScore.text = data.basicInfo.liveInfo.score

            // TODO 賠率待實作

            tvWatchCount.text = data.basicInfo.liveInfo.viewerCount.toString()
        }
    }
}