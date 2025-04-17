package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.enums.PlayType

class HomePagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle,
                       private val fragments: List<PlayType>
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return  if (fragments[position].fragment == null) { throw NullPointerException() } else { fragments[position].fragment!! }
    }
}