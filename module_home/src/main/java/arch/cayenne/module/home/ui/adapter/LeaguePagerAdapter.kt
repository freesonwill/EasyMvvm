package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var tournament: List<TournamentDataModel>,
    private val playTypeId: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemId(position: Int): Long {
        return playTypeId * 10000L + position
    }

    override fun getItemCount(): Int = tournament.size

    override fun createFragment(position: Int): Fragment {
        val list = tournament
        val sportId = list[position].sportId
        val leagueId = list[position].id
        return MatchListPagerFragment.newInstance(sportId, playTypeId, leagueId, position)
    }
}
