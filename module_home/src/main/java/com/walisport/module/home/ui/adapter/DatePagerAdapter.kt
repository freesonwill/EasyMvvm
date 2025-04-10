package com.walisport.module.home.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walisport.module.home.ui.fragment.EarlyGameListFragment

class DatePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val dateList: List<Triple<Int, String, String>>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = dateList.size

    override fun createFragment(position: Int): Fragment {
        val (leagueId,date) = dateList[position]
        return EarlyGameListFragment.newInstance(leagueId, date)
    }
}
