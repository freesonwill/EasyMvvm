package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.hall.R
import com.walisport.module.hall.data.HallGameTabDefault
import com.walisport.module.hall.databinding.ItemGameAllRankingBinding
import com.walisport.module.hall.ui.fragment.GameAllRankingListFragment
import com.walisport.module.hall.ui.fragment.GameRankingInfoDialogFragment

class GameAllRankingAdapter(
    val parentFragmentManager : androidx.fragment.app.FragmentManager,
    val childFragmentManager : androidx.fragment.app.FragmentManager,
    val lifecycle: androidx.lifecycle.Lifecycle,
) : RecyclerView.Adapter<GameAllRankingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllRankingViewHolder {
        val binding = ItemGameAllRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllRankingViewHolder(parentFragmentManager, childFragmentManager, lifecycle, binding)
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
    val parentFragmentManager : androidx.fragment.app.FragmentManager,
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
            TabLayoutMediator(tlRanking, vpRanking) { tab, position ->
                tab.text = mockTabList[position].title
            }.attach()
            vpRanking.setupViewPagerScroll(tlRanking, homeIndicator, 1f)

            ivRankingInfo.clickNoRepeat {
                val location = IntArray(2)
                ivRankingInfo.getLocationInWindow(location)
                val h = ViewUtils.getStatusBarHeight(item.root.context)
                val positionX = location.first() + ivRankingInfo.width / 2
                val positionY = location.last() - h - 1.dp2px
                GameRankingInfoDialogFragment.newInstance(
                    positionX,
                    positionY,
                ).show(parentFragmentManager)
            }
        }
    }
}

