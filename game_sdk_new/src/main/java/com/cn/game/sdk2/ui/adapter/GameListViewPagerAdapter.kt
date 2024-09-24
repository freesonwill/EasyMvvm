package com.cn.game.sdk2.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cn.game.sdk2.data.bean.PagerBean
import com.cn.game.sdk2.ui.page.fast3.Fast3GameHallItemFragment

class GameListViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val gameTypes: List<Int>
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int = gameTypes.size

    override fun createFragment(position: Int): Fragment = Fast3GameHallItemFragment.newInstance(gameTypes[position])
}