package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.ui.adapter.compare.TournamentListCallback
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var tournament: List<TournamentDataModel>,
    private val playType: PlayType
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemId(position: Int): Long {
        return playType.id * 10000L + position
    }

    override fun getItemCount(): Int = tournament.size

    fun updateList(newList: List<TournamentDataModel>) {
        val diffResult = DiffUtil.calculateDiff(TournamentListCallback(tournament, newList))
        tournament = newList
        diffResult.dispatchUpdatesTo(this)
    }
    override fun createFragment(position: Int): Fragment {
        val list = tournament
        val sportId = list[position].sportId
        val leagueId = list[position].id
        return MatchListPagerFragment.newInstance(sportId, playType.id, leagueId, position)
    }
}
