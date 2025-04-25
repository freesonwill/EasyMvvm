package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.ui.fragment.EarlyGameListFragment
import arch.cayenne.module.home.ui.fragment.TodayGameListFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val playType: PlayType,
    private val getSelectedDate: ((Int) -> String)? = null // 可選
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var tournament: List<TournamentDataModel>? = null

    fun setData(list: List<TournamentDataModel>) {
        tournament = list
        notifyItemRangeChanged(0, list.size-1)
    }

    override fun getItemCount(): Int = tournament?.size ?: 0

    override fun createFragment(position: Int): Fragment {
        if (tournament.isNullOrEmpty()) {
            throw IllegalStateException("league list is null or empty")
        }
        val leagueId = tournament!![position].id
        val sportId = tournament!![position].sportId
        "KC_ $sportId".loge("KC_")
        return when (playType) {
            PlayType.TODAY -> TodayGameListFragment.newInstance(sportId, leagueId)
            PlayType.EARLY -> {
                val date = getSelectedDate?.invoke(leagueId) ?: ""
                EarlyGameListFragment.newInstance(sportId, leagueId, date)
            }

            else -> throw IllegalStateException("CHAMPION tab does not support league pager")//暫時不需要
        }
    }
}
