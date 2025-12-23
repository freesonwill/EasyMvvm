package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import com.google.android.material.tabs.TabLayout
import com.walisport.module.hall.R
import com.walisport.module.hall.data.HallGameTab
import com.walisport.module.hall.data.HallGameTabDefault
import com.walisport.module.hall.databinding.ItemGameAllRankingBinding
import com.walisport.module.hall.ui.fragment.LatestBetFragment
import com.walisport.module.hall.ui.fragment.GameAllRankingTodayFragment
import com.walisport.module.hall.ui.fragment.GameRankingInfoDialogFragment
import com.walisport.module.hall.ui.fragment.HighStakesFragment

class GameAllRankingAdapter(
    val parentFragmentManager : androidx.fragment.app.FragmentManager,
    val childFragmentManager : androidx.fragment.app.FragmentManager,
    val lifecycle: androidx.lifecycle.Lifecycle,
    val onPageChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<GameAllRankingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllRankingViewHolder {
        val binding = ItemGameAllRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllRankingViewHolder(parentFragmentManager, childFragmentManager, lifecycle, binding) {
            onPageChanged?.invoke()
        }
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
    val item: ItemGameAllRankingBinding,
    val onPageChanged: (() -> Unit)? = null
): BaseViewHolder(item) {
    private val mockTabList = arrayListOf(
        HallGameTab(
            _title = R.string.tab_ranking_newest.getString(),
            _page = { LatestBetFragment.newInstance() }
        ),
        HallGameTab(
            _title = R.string.tab_ranking_biggest.getString(),
            _page = { HighStakesFragment.newInstance() }
        ),
        HallGameTab(
            _title = R.string.tab_ranking_today.getString(),
            _page = { GameAllRankingTodayFragment.newInstance() }
        )
    )
    @SuppressLint("ClickableViewAccessibility")
    fun bind() {
        with(item) {
            vpRanking.adapter = PagerAdapter(childFragmentManager, lifecycle, mockTabList)
            vpRanking.setupHorizontalScrollDegree(0)
            tlRanking.removeAllTabs()
            mockTabList.forEach { m ->
                val tab = tlRanking.newTab()
                tab.text = m.title
                tlRanking.addTab(tab)
            }
            vpRanking.offscreenPageLimit = 3
            tlRanking.post {
                val tabWidth = tlRanking.width.toFloat() / tlRanking.tabCount
                homeIndicator.setTabWidth(tabWidth, 1f)
            }
            vpRanking.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 假设 pagerAdapter 是你的 PagerAdapter 实例
                    val pagerAdapter = vpRanking.adapter as? PagerAdapter
                    val fragment = pagerAdapter?.getFragment(position)
                    if (fragment is GameAllRankingTodayFragment) {
                        val height = fragment.getContentHeight()
                        vpRanking.layoutParams.height = height
                        vpRanking.requestLayout()
                        LogUtils.e("√", "onPageSelected height=$height")
                    } else if (fragment is LatestBetFragment) {
                        val height = fragment.getContentHeight()
                        vpRanking.layoutParams.height = height
                        vpRanking.requestLayout()
                        LogUtils.e("GameAllRankingViewHolder", "onPageSelected height=$height")
                    } else if (fragment is HighStakesFragment) {
                        val height = fragment.getContentHeight()
                        vpRanking.layoutParams.height = height
                        vpRanking.requestLayout()
                        LogUtils.e("GameAllRankingViewHolder", "onPageSelected height=$height")
                    }


                }
            })

            tlRanking.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    vpRanking.startFadeAnim { onComplete ->
                        vpRanking.setCurrentItem(tab?.position?:0, false)
                        onComplete.invoke()
                    }
                    CustomTabIndicatorUtils.animateIndicatorToPosition(
                        homeIndicator,
                        tab?.position?:0
                    )
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            ivRankingInfo.addScaleOnTouchAnimation()
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

