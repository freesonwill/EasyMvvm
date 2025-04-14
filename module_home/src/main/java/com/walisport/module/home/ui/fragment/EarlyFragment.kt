package com.walisport.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.home.R
import com.walisport.module.home.databinding.FragmentEarlyBinding
import com.walisport.module.home.databinding.ItemDateTabBinding
import com.walisport.module.home.databinding.ItemLeagueTabBinding
import com.walisport.module.home.enums.HomeTab
import com.walisport.module.home.enums.LeagueType
import com.walisport.module.home.manager.DateTabManager
import com.walisport.module.home.ui.adapter.LeaguePagerAdapter
import com.walisport.module.home.utils.DateUtils.getNext7Days
import kotlin.reflect.KClass

class EarlyFragment : BaseFragment<EmptyViewModel, FragmentEarlyBinding>() {
    override val vbClass: KClass<FragmentEarlyBinding> = FragmentEarlyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    private val leagues = mutableListOf<LeagueType>()
    private val dateTabManager = DateTabManager()
    private lateinit var leagueAdapter: LeaguePagerAdapter

    override fun initView(savedInstanceState: Bundle?) {
        // 預設 "全部"
        leagues.clear()
        leagues.add(LeagueType.ALL)
        val apiLeagueIds = listOf(1, 2, 3, 4) // 模擬 API 返回的聯賽 ID
        leagues.addAll(apiLeagueIds.mapNotNull { LeagueType.fromId(it) })

        val dateTabs = getNext7Days() // 取得未來 7 天 (MMDD, 星期)

        with(mBinding) {
            //聯賽
            leagueAdapter = LeaguePagerAdapter(
                childFragmentManager,
                lifecycle,
                leagues,
                HomeTab.EARLY,
            ) { leagueId ->
                dateTabManager.getDateString(leagueId, dateTabs)
            }
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
                    vpGameList.currentItem = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            // 日期 Tab 設定
            updateDateTabs(tlDateList, dateTabs)
            tlDateList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val dateTabIndex = tab?.position ?: 0
                    val position = vpGameList.currentItem
                    val leagueId = leagues[position].leagueId

                    // 記錄當前聯賽所選的 tab index
                    dateTabManager.setSelectedIndex(leagueId, dateTabIndex)

                    // 需實作ViewModel更新對應賽事列表頁頁面

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            //ViewPager
            vpGameList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val leagueId = leagues[position].leagueId
                    mBinding.tlLeagueList.getTabAt(position)?.select()
                    // 找到該聯賽目前記錄的日期 tab index
                    updateDateTabs(tlDateList, dateTabs)
                    tlDateList.getTabAt(dateTabManager.getSelectedIndex(leagueId))?.select()
                }
            })

            vpGameList.post {
                vpGameList.currentItem = 0
                tlLeagueList.getTabAt(0)?.select()
                tlDateList.getTabAt(0)?.select()
            }
        }
    }

    private fun updateDateTabs(tlDateList: TabLayout, dateTabs: List<Pair<String, String>>) {
        tlDateList.apply {
            removeAllTabs()

            // 加入 "全部" tab
            addTab(newTab().apply {
                val tabView = ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabView.tvDate.text = "全部"
                tabView.tvWeekDay.text = ""
                tabView.root.setBackgroundResource(R.drawable.selector_date_tab_bg)
                customView = tabView.root
            })

            // 加入其餘日期 tabs
            dateTabs.forEach { (date, weekday) ->
                val tab = newTab()
                val tabView = ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabView.tvDate.text = date
                tabView.tvWeekDay.text = weekday
                tabView.root.setBackgroundResource(R.drawable.selector_date_tab_bg)
                tab.customView = tabView.root
                addTab(tab)
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}