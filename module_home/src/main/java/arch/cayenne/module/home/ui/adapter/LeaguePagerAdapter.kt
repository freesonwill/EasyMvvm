package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var playType: PlayType
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var tournament: List<TournamentDataModel>? = null

    fun setData(list: List<TournamentDataModel>) {
        tournament = list
        notifyItemRangeChanged(0, list.size - 1)
    }
    fun setPlayType(newPlayType: PlayType) {
        playType = newPlayType
    }

    override fun getItemId(position: Int): Long {
        return playType.id * 10000L + position
    }

    override fun getItemCount(): Int = tournament?.size ?: 0

    override fun createFragment(position: Int): Fragment {
        val list = tournament ?: throw IllegalStateException("tournament list is null")
        val sportId = list[position].sportId
        val leagueId = list[position].id
        return MatchListPagerFragment.newInstance(sportId, playType.id, leagueId)
    }
}
