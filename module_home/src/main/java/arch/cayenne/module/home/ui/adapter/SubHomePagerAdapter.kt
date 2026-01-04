package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.data.constants.MatchListSortType
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.ui.fragment.ChampionSubFragment
import arch.cayenne.module.home.ui.fragment.CollectListFragment
import arch.cayenne.module.home.ui.fragment.EarlyFragment
import arch.cayenne.module.home.ui.fragment.SubHomeFragmentV2
import arch.cayenne.module.home.ui.fragment.SubHomeFragment
import arch.cayenne.module.home.ui.fragment.SuperCompetitionFragment

class SubHomePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val promoCount: Int,
    val playTypes: List<PlayType>,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = promoCount + playTypes.size

    override fun createFragment(position: Int): Fragment {
        if (position < promoCount) return SuperCompetitionFragment.newInstance(
            SportType.SOCCER.id,
            if (position == 0) {
                listOf(8)
            } else {
                listOf(37)
            }
        )
        return when (val playType = playTypes[position - promoCount]) {
            PlayType.FAVORITE -> CollectListFragment()
            PlayType.EARLY -> EarlyFragment.newInstance(playType.id)
            PlayType.ROLLING -> SubHomeFragmentV2.newInstance(playType.id, MatchListSortType.BY_HOT)
            PlayType.TODAY -> SubHomeFragmentV2.newInstance(playType.id, MatchListSortType.BY_HOT)
            PlayType.CHAMPION -> ChampionSubFragment.newInstance(playType.id)
            else -> SubHomeFragment.newInstance(
                playType.id
            )
        }
    }
}
