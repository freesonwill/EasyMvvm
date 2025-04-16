package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.extension.sharedViewModel
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentEarlyBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.LeagueType
import arch.cayenne.module.home.manager.DateTabManager
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.utils.DateUtils.getNext7Days
import arch.cayenne.module.home.viewmodel.EarlyViewModel
import com.bumptech.glide.Glide
import arch.cayenne.module.home.utils.DateUtils.getFutureDays
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale
import kotlin.reflect.KClass

class EarlyFragment : BaseFragment<EarlyViewModel, FragmentEarlyBinding>() {
    override val vbClass: KClass<FragmentEarlyBinding> = FragmentEarlyBinding::class
    override val vmClass: KClass<EarlyViewModel> = EarlyViewModel::class
    // TODO viewmodel待實作, 串接資料後再依據mvvm架構重構

    private val leagues = mutableListOf<LeagueType>()
    private val dateTabManager = DateTabManager()
    private val leagueAdapter: LeaguePagerAdapter by lazy { LeaguePagerAdapter(childFragmentManager, lifecycle, PlayType.TODAY){ leagueId ->
        dateTabManager.getDateString(leagueId, getNext7Days())
    } }
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    override fun initView(savedInstanceState: Bundle?) {
        // 預設 "全部"
        leagues.clear()
        leagues.add(LeagueType.ALL)
        val apiLeagueIds = listOf(1, 2, 3, 4) // 模擬 API 返回的聯賽 ID
        leagues.addAll(apiLeagueIds.mapNotNull { LeagueType.fromId(it) })

        val dateTabs =
            getFutureDays(7, Locale.getDefault()) // List<Pair<String, String>> → (MMDD, 星期)
        dateTabManager.setSelectedIndex(LeagueType.ALL.leagueId, 0)
        setupDateTabs(dateTabs)
        setupLeagueViewPager(leagues, dateTabs)
    }

    private fun setupLeagueViewPager(
        leagues: List<LeagueType>,
        dateTabs: List<Pair<String, String>>
    ) {
        with(mBinding) {
            //聯賽
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null

            vpGameList.adapter = leagueAdapter

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

            fun createTab(date: String?, weekday: String?): TabLayout.Tab {
                val tab = newTab()
                val tabView =
                    ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false).apply {
                        root.layoutParams = ViewGroup.LayoutParams(56.dp2px, 50.dp2px)
                        tvDate.text = date ?: context.getString(R.string.tab_text_all)
                        tvWeekDay.visibility = if (weekday == null) View.GONE else View.VISIBLE
                        tvWeekDay.text = weekday
                    }
                tab.customView = tabView.root
                return tab
            }

            // 建立 tabs
            addTab(createTab(null, null)) // "全部"
            dateTabs.forEach { (date, weekday) -> addTab(createTab(date, weekday)) }

            // 移除預設 margin 並設定背景與 padding
            post {
                val tabStrip = getChildAt(0) as? LinearLayout ?: return@post
                for (i in 0 until tabStrip.childCount) {
                    tabStrip.getChildAt(i).apply {
                        layoutParams = LinearLayout.LayoutParams(56.dp2px, 50.dp2px).apply {
                            setMargins(5.dp2px, 0, 0, 0)
                        }
                        setPadding(0, 0, 0, 0)
                        setBackgroundResource(R.drawable.selector_date_tab_bg)
                    }
                }
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.currentSportChange.observe(this) {
            mViewModel.setCurrentSport(it)
            mViewModel.getCurrentTournament(it)
        }
        mViewModel.tournaments.observe(this) {
            initLeaguesLayout(it)
        }
    }

    private fun initLeaguesLayout(tournaments: List<TournamentDataModel>) {
        mBinding.apply {
            leagueAdapter.setData(tournaments)
            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val tournament = tournaments[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    Glide.with(this@EarlyFragment).load(tournament.icon).into(ivLeagueIcon)
                    tvLeagueName.text = tournament.simpleName

                    ivLeagueIcon.imageTintList = context?.let {
                        SportSkinResourceManager.getColorStateList(
                            it,
                            R.color.selector_league_tab_tint
                        )
                    }
                    if (tournament.id == LeagueType.ALL.leagueId) {
                        ivLeagueIcon.visibility = View.GONE
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
        }

    }
}