package arch.cayenne.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.module.home.enums.HomeTab

class HomePagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle,
                       private val fragments: List<HomeTab>
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return  fragments[position].fragment
    }
}