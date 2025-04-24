package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.common.extension.sharedViewModel
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentEarlyBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.manager.DateTabManager
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.utils.DateUtils.getFutureDays
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel.Companion.TOURNAMENT_ALL_ID
import arch.cayenne.module.home.viewmodel.EarlyViewModel
import arch.cayenne.module.home.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale
import kotlin.reflect.KClass

class EarlyFragment : BaseFragment<EarlyViewModel, FragmentEarlyBinding>() {
    override val vbClass: KClass<FragmentEarlyBinding> = FragmentEarlyBinding::class
    override val vmClass: KClass<EarlyViewModel> = EarlyViewModel::class
    private lateinit var leagueAdapter: LeaguePagerAdapter
    private val dateTabManager = DateTabManager()
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    override fun initView(savedInstanceState: Bundle?) {
        val dateTabs = getFutureDays(7, Locale.getDefault()) // 取得未來 7 天 (MMDD, 星期)
        with(mBinding) {
            //聯賽
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null
            leagueAdapter = LeaguePagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                PlayType.TODAY
            )
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
                    val leagueId = leagueAdapter.getItemId(position).toInt()

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
                    val leagueId = leagueAdapter.getItemId(position).toInt()
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

            addTab(createTab(null, null))
            dateTabs.forEach { (date, weekday) -> addTab(createTab(date, weekday)) }

            // 調整間距與樣式
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
            if (homeViewModel.getCurrentPlayType() != PlayType.EARLY) return@observe

            mViewModel.setCurrentSport(it)
            mViewModel.getCurrentTournament(it)
        }
        mViewModel.tournaments.observe(this) {
            initLeaguesLayout(it)
        }
    }

    //需增加"全部"
    private fun initLeaguesLayout(tournaments: List<TournamentDataModel>) {
        mBinding.apply {
            leagueAdapter.setData(tournaments)
            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val tournament = tournaments[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    if (tournament.id == TOURNAMENT_ALL_ID) {   //ALL 標籤
                        ivLeagueIcon.visibility = View.GONE
                        tvLeagueName.text = getString(R.string.league_all)
                    } else {
                        Glide.with(this@EarlyFragment).load(tournament.icon).into(ivLeagueIcon)
                        tvLeagueName.text = tournament.simpleName
                        ivLeagueIcon.imageTintList = context?.let {
                            SportSkinResourceManager.getColorStateList(
                                it,
                                R.color.selector_league_tab_tint
                            )
                        }
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