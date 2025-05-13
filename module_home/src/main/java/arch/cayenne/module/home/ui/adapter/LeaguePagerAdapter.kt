package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.ui.fragment.ChampionFragment
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val tournament: List<TournamentDataModel>,
    private val playType: PlayType
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemId(position: Int): Long {
        return playType.id * 10000L + position
    }

    override fun getItemCount(): Int {
        return if (playType == PlayType.CHAMPION) 1 else tournament.size
    }

    override fun containsItem(itemId: Long): Boolean {
        return true // 保守做法，讓所有 item 都保留
    }

    override fun createFragment(position: Int): Fragment {
        return if (playType == PlayType.CHAMPION) {
            val sportId = tournament.firstOrNull()?.sportId ?: 0 // 安全取得
            ChampionFragment.newInstance(sportId)
        } else {
            val list = tournament
            val sportId = list[position].sportId
            val leagueId = list[position].id
            MatchListPagerFragment.newInstance(sportId, playType.id, leagueId, position)
        }
    }
}
