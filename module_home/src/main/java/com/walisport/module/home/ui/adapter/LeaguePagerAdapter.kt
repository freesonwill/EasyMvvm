package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walisport.module.home.enums.LeagueType
import arch.cayenne.module_home.ui.fragment.TodayGameListFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val leagues: List<LeagueType>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = leagues.size

    override fun createFragment(position: Int): Fragment {
        return TodayGameListFragment.newInstance(leagues[position].leagueId)
    }
}
