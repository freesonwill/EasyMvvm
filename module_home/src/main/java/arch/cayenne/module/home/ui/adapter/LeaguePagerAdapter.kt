package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.ui.fragment.MatchListPagerFragment

class LeaguePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var tournament = arrayListOf<TournamentDataModel>()
    private var playTypeId: Int = PlayType.TODAY.id

    @SuppressLint("NotifyDataSetChanged")
    fun setData(playTypeId: Int, list: List<TournamentDataModel>) {
        this.playTypeId = playTypeId
        tournament = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return tournament[position].sportId * 10000 + position.toLong()
    }

    override fun getItemCount(): Int = tournament.size

    override fun createFragment(position: Int): Fragment {
        val list = tournament
        val sportId = list[position].sportId
        val leagueId = list[position].id
        return MatchListPagerFragment.newInstance(sportId, playTypeId, leagueId, position)
    }
}
