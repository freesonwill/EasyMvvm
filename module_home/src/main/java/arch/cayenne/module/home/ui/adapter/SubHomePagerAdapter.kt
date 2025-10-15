package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.ui.fragment.CollectListFragment
import arch.cayenne.module.home.ui.fragment.SubHomeFragment

class SubHomePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val playTypes: List<PlayType>,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = playTypes.size

    override fun createFragment(position: Int): Fragment {
        val playType = playTypes[position]
        return if (playType == PlayType.FAVORITE) {
            CollectListFragment()
        } else {
            SubHomeFragment.newInstance(playType.id)
        }
    }
}
