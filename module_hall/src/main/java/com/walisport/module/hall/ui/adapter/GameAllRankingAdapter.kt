package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.hall.R
import com.walisport.module.hall.data.HallGameTabDefault
import com.walisport.module.hall.databinding.ItemGameAllRankingBinding

class GameAllRankingAdapter(
    val childFragmentManager : androidx.fragment.app.FragmentManager,
    val lifecycle: androidx.lifecycle.Lifecycle,
) : RecyclerView.Adapter<GameAllRankingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllRankingViewHolder {
        val binding = ItemGameAllRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllRankingViewHolder(childFragmentManager, lifecycle, binding)
    }

    override fun onBindViewHolder(
        holder: GameAllRankingViewHolder,
        position: Int
    ) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1



}

class GameAllRankingViewHolder(
    val childFragmentManager : androidx.fragment.app.FragmentManager,
    val lifecycle: androidx.lifecycle.Lifecycle,
    val item: ItemGameAllRankingBinding
): BaseViewHolder(item) {
    private val mockTabList = arrayListOf(
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_recent,
            _title = R.string.tab_ranking_newest.getString(),
            _page = { GameAllRankingListFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_all,
            _title = R.string.tab_ranking_biggest.getString(),
            _page = { GameAllRankingListFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_table,
            _title = R.string.tab_ranking_today.getString(),
            _page = { GameAllRankingListFragment.newInstance() }
        )
    )
    @SuppressLint("ClickableViewAccessibility")
    fun bind() {
        with(item) {
            vpRanking.adapter = PagerAdapter(childFragmentManager, lifecycle, mockTabList)
            vpRanking.isUserInputEnabled = true
            vpRanking.getChildAt(0).setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
            TabLayoutMediator(tlRanking, vpRanking) { tab, position ->
                tab.text = mockTabList[position].title
            }.attach()
            vpRanking.setupViewPagerScroll(tlRanking, homeIndicator, 1f)
        }
    }
}

