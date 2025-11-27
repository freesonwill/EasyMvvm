package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.TournamentCombo
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.ui.fragment.EarlyMatchListPagerFragment
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var tournamentCombos = arrayListOf<TournamentCombo>()
    private var playTypeId: Int = PlayType.TODAY.id

    @SuppressLint("NotifyDataSetChanged")
    fun setData(playTypeId: Int, comboList: List<TournamentCombo>) {
        this.playTypeId = playTypeId
        tournamentCombos = ArrayList(comboList)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
//        return tournament[position].id * 10000 + position.toLong()
        return position.toLong()
    }

    override fun getItemCount(): Int = tournamentCombos.size

    override fun createFragment(position: Int): Fragment {
        val combos = tournamentCombos
        val sportId = combos[position].sportId
        val leagueIdList = combos[position].leagueIdList
        return if (playTypeId == PlayType.EARLY.id) {
            EarlyMatchListPagerFragment.newInstance(sportId, playTypeId, leagueIdList, position)
        } else {
            MatchListPagerFragment.newInstance(sportId, playTypeId, leagueIdList, position)
        }
    }
}
