package com.walisport.module.search.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.ui.fragment.SearchResultPageFragment
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_ALL
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_TOURNAMENT
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_TEAM
import com.walisport.module.search.ui.fragment.SearchResultPageFragment.Companion.TYPE_PLAYER

class SearchResultPagerAdapter(
    fragment: Fragment,
    val data: SearchResultBean,
    private val onItemClick: (id: String, keyword: String, type: SearchTypeEnum) -> Unit
): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchResultPageFragment.newInstance(TYPE_ALL, data, onItemClick)
            1 -> SearchResultPageFragment.newInstance(TYPE_TOURNAMENT, data, onItemClick)
            2 -> SearchResultPageFragment.newInstance(TYPE_TEAM, data, onItemClick)
            3 -> SearchResultPageFragment.newInstance(TYPE_PLAYER, data, onItemClick)
            else -> Fragment()
        }
    }

}