package com.walisport.module_home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walisport.module_home.data.HomeTabs

class HomePagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle,
                       private val fragments: List<HomeTabs>
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int = HomeTabs.entries.size

    override fun createFragment(position: Int): Fragment {
        return  fragments[position].fragment
    }
}