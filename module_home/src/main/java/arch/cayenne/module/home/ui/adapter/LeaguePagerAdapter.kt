package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.enums.HomeTab
import arch.cayenne.module.home.enums.LeagueType
import arch.cayenne.module.home.ui.fragment.EarlyGameListFragment
import arch.cayenne.module.home.ui.fragment.TodayGameListFragment

//class LeaguePagerAdapter(
//    fragmentManager: FragmentManager,
//    lifecycle: Lifecycle,
//    private val leagues: List<LeagueType>
//) : FragmentStateAdapter(fragmentManager, lifecycle) {
////
//    override fun getItemCount(): Int = leagues.size
//
//    override fun createFragment(position: Int): Fragment {
//        return TodayGameListFragment.newInstance(leagues[position].leagueId)
//    }
//}
class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val leagues: List<LeagueType>,
    private val homeTab: HomeTab,
    private val getSelectedDate: ((Int) -> String)? = null // 可選
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = leagues.size

    override fun createFragment(position: Int): Fragment {
        val leagueId = leagues[position].leagueId
        return when (homeTab) {
            HomeTab.TODAY -> TodayGameListFragment.newInstance(leagueId)
            HomeTab.EARLY -> {
                val date = getSelectedDate?.invoke(leagueId) ?: ""
                EarlyGameListFragment.newInstance(leagueId, date)
            }

            HomeTab.CHAMPION -> throw IllegalStateException("CHAMPION tab does not support league pager")//暫時不需要
        }
    }
}
