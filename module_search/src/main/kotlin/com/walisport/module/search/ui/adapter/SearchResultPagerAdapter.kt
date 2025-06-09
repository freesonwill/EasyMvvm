package com.walisport.module.search.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.ui.fragment.SearchResultPageFragment
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_ALL
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_TOURNAMENT
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_TEAM
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_PLAYER

class SearchResultPagerAdapter(fragment: Fragment, val data: SearchResultBean): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchResultPageFragment.newInstance(TYPE_ALL, data)
            1 -> SearchResultPageFragment.newInstance(TYPE_TOURNAMENT, data)
            2 -> SearchResultPageFragment.newInstance(TYPE_TEAM, data)
            3 -> SearchResultPageFragment.newInstance(TYPE_PLAYER, data)
            else -> Fragment()
        }
    }

}