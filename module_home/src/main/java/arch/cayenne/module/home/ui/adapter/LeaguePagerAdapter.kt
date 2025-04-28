package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val getSelectedDate: ((Int) -> String)? = null
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var tournament: List<TournamentDataModel>? = null

    fun setData(list: List<TournamentDataModel>) {
        tournament = list
        notifyItemRangeChanged(0, list.size - 1)
    }

    override fun getItemCount(): Int = tournament?.size ?: 0

    override fun createFragment(position: Int): Fragment {
        val list = tournament ?: throw IllegalStateException("tournament list is null")
        val sportId = list[position].sportId
        val leagueId = list[position].id
        val date = getSelectedDate?.invoke(leagueId)
        return MatchListPagerFragment.newInstance(sportId, leagueId, date)
    }
}
