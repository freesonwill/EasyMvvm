package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.ui.fragment.CollectListFragment
import arch.cayenne.module.home.ui.fragment.EarlyFragment
import arch.cayenne.module.home.ui.fragment.PromoPlaceholderFragment
import arch.cayenne.module.home.ui.fragment.SubHomeFragment

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
        if (position < promoCount) return PromoPlaceholderFragment()
        return when (val playType = playTypes[position - promoCount]) {
            PlayType.FAVORITE -> CollectListFragment()
            PlayType.EARLY -> EarlyFragment.newInstance(playType.id)
            else -> SubHomeFragment.newInstance(
                playType.id
            )
        }
    }
}
