package com.walisport.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import arch.cayenne.lib.common.utils.ViewUtils
import com.walisport.module.home.R
import com.walisport.module.home.databinding.FragmentEarlyBinding
import com.walisport.module.home.databinding.ItemDateTabBinding
import com.walisport.module.home.databinding.ItemLeagueTabBinding
import com.walisport.module.home.enums.LeagueType
import com.walisport.module.home.ui.adapter.DatePagerAdapter
import com.walisport.module.home.utils.DateUtils.getNext7Days
import kotlin.reflect.KClass

class EarlyFragment : BaseFragment<EmptyViewModel, FragmentEarlyBinding>() {
    override val vbClass: KClass<FragmentEarlyBinding> = FragmentEarlyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private var leagues = mutableListOf<Triple<Int, String, String>>() // (日期, leagueId)

    private lateinit var leagueAdapter: LeaguePagerAdapter
    private lateinit var dateAdapter: DatePagerAdapter

    // 模擬 API 返回的日期範圍
//    val dateList = listOf(
//        "全部" to "",
//        "04.08" to "星期一",
//        "04.09" to "星期二",
//        "04.10" to "星期三",
//        "04.11" to "星期四"
//    )
    private val dateList = getNext7Days()
    val apiDates = listOf(20240408, 20240409, 20240410, 20240411)
    val apiLeagueIds = listOf(1, 2, 3, 4)
    override fun initView(savedInstanceState: Bundle?) {
        // 預設 "全部"
        leagues.apply {
            add(Triple(LeagueType.ALL.leagueId, "全部", "")) // 預設 "全部"，無日期 & 星期

            val apiDates = getNext7Days() // 取得未來 7 天 (MMDD, 星期)
            val apiLeagueIds = listOf(1, 2, 3, 4) // 模擬 API 返回的聯賽 ID

            apiDates.forEach { (date, weekday) ->
                apiLeagueIds.forEach { leagueId ->
                    add(Triple(leagueId, date, weekday))
                }
            }
        }

        with(mBinding) {
            leagueAdapter = LeaguePagerAdapter(childFragmentManager, lifecycle, leagues)
            vpGameList.adapter = leagueAdapter

            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val league = leagues[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    league.iconRes?.let { ivLeagueIcon.setImageResource(it) }
                    tvLeagueName.setText(league.titleRes)

                    ivLeagueIcon.imageTintList = context?.let {
                        SportSkinResourceManager.getColorStateList(
                            it,
                            R.color.selector_league_tab_tint
                        )
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

            dateAdapter = DatePagerAdapter(childFragmentManager, lifecycle, leagues)
            vpGameList.adapter = dateAdapter

            // 設置 "聯賽 + 日期" Tab
            TabLayoutMediator(tlDateList, vpGameList) { tab, position ->
                val (leagueId, date, weekday) = leagues[position] // 取出數據

                val tabBinding =
                    ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    tvDate.text = if (leagueId == LeagueType.ALL.leagueId) "全部" else date
                    tvWeekDay.text = weekday

                    root.setBackgroundResource(R.drawable.selector_date_tab_bg)
                }

                tab.customView = tabBinding.root
                tab.view.setPadding(
                    0,
                    0,
                    ViewUtils.dpToPx(10f).toInt(),
                    0
                )
            }.attach()

            tlDateList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.isSelected = true
                    vpGameList.currentItem = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            vpGameList.post {
                vpGameList.currentItem = 0
                tlLeagueList.getTabAt(0)?.select()
                tlDateList.getTabAt(0)?.select()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}