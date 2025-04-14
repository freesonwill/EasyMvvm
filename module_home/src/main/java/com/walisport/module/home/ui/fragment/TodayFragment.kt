package com.walisport.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.skin.res.SportSkinResourceManager.getColorStateList
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.home.R
import com.walisport.module.home.databinding.FragmentTodayBinding
import com.walisport.module.home.databinding.ItemLeagueTabBinding
import com.walisport.module.home.enums.HomeTab
import com.walisport.module.home.enums.LeagueType
import com.walisport.module.home.ui.adapter.LeaguePagerAdapter
import kotlin.reflect.KClass

class TodayFragment : BaseFragment<EmptyViewModel, FragmentTodayBinding>() {
    override val vbClass: KClass<FragmentTodayBinding> = FragmentTodayBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private val leagues = mutableListOf<LeagueType>()
    private lateinit var leagueAdapter: LeaguePagerAdapter
    override fun initView(savedInstanceState: Bundle?) {
        // 預設 "全部"
        leagues.add(LeagueType.ALL)

        // 模擬 API 返回的聯賽 ID
        val apiLeagueIds = listOf(1, 2, 3, 4)
        leagues.addAll(apiLeagueIds.mapNotNull { LeagueType.fromId(it) })
        with(mBinding) {
            leagueAdapter = LeaguePagerAdapter(childFragmentManager, lifecycle, leagues, HomeTab.TODAY)
            vpGameList.adapter = leagueAdapter

            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val league = leagues[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    league.iconRes?.let { ivLeagueIcon.setImageResource(it) }
                    tvLeagueName.setText(league.titleRes)

                    ivLeagueIcon.imageTintList = context?.let {
                        getColorStateList(it, R.color.selector_league_tab_tint)
                    }
                    if (league.leagueId == LeagueType.ALL.leagueId) {
                        ivLeagueIcon.visibility = View.GONE
                    } else {
                        league.iconRes?.let { ivLeagueIcon.setImageResource(it) }
                    }

                    root.setBackgroundResource(R.drawable.selector_league_tab_bg)
                }

                tab.customView = tabBinding.root
                tab.view.setPadding(
                    0,
                    0,
                    ViewUtils.dpToPx(10f).toInt(),
                    0
                )
                tab.view.setOnClickListener {
                    //傳聯賽id索取賽事列表資料更新列表
                }
            }.attach()

            tlLeagueList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.isSelected = true
//                    tab?.view?.isSelected = true
                    vpGameList.currentItem = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            vpGameList.post {
                vpGameList.currentItem = 0
                tlLeagueList.getTabAt(0)?.select()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}