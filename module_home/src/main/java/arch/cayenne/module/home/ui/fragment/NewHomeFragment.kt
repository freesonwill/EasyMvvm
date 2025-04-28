package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.utils.DateUtils
import arch.cayenne.module.home.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private val fragments = mutableMapOf<PlayType, Fragment>()
    private var isFirstTime = true
    private val sportsListAdapter by lazy {
        SportsListAdapter { sport ->
            Toast.makeText(
                requireContext(),
                "選擇：${getString(SportType.fromId(sport.id)!!.titleResId)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private lateinit var leagueAdapter: LeaguePagerAdapter

    override fun initView(savedInstanceState: Bundle?) {

        childFragmentManager.beginTransaction()
            .replace(mBinding.floatingContainer.id, FloatingButtonFragment())
            .commit()
        initPlayTypeLayout()
        initSportLayout()
        initTournamentLayout()
    }

    //init 一級導航欄位
    private fun initPlayTypeLayout() {
        with(mBinding) {
            PlayType.entries.forEach {
                tlHome.addTab(tlHome.newTab().setText(it.titleRes))
            }
            tlHome.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.apply {
                        if (PlayType.entries[this] == PlayType.TODAY) {
                            mBinding.layoutContainer.tlDateList.visibility = View.GONE
                        } else if (PlayType.entries[this] == PlayType.EARLY) {
                            mBinding.layoutContainer.tlDateList.visibility = View.VISIBLE
                        }
                        mViewModel.setCurrentPlayType(PlayType.entries[this])
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

        }
    }

    //init 二級導航欄位
    private fun initSportLayout() {
        mBinding.apply {
            rvSportsList.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = sportsListAdapter
            }
        }
    }

    //init 三級導航欄位與日期
    private fun initTournamentLayout() {
        val dateTabs = DateUtils.getFutureDays(7, Locale.getDefault()) // 取得未來 7 天 (MMDD, 星期)
        with(mBinding.layoutContainer) {
            //聯賽
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null
            leagueAdapter = LeaguePagerAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle
            )
            vpGameList.adapter = leagueAdapter

            tlLeagueList.addOnTabSelectedListener(object : OnTabSelectedListener {
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
            tlDateList.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val dateTabIndex = tab?.position ?: 0
                    val dateString = if (dateTabIndex == 0) "" else {
                        val datePair = DateUtils.getFutureDays(7, Locale.getDefault())
                            .getOrNull(dateTabIndex - 1)
                        datePair?.first.orEmpty() // MMdd 格式
                    }
                    // 記錄當前聯賽所選的 tab index
                    mViewModel.setSelectedDate(dateString)

                    // 需實作ViewModel更新對應賽事列表頁頁面

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            //ViewPager
            vpGameList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tlLeagueList.getTabAt(position)?.select()
                    // 找到該聯賽目前記錄的日期 tab index
                    updateDateTabs(tlDateList, dateTabs)
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

    private fun initLeaguesLayout(tournaments: List<TournamentDataModel>) {
        mBinding.layoutContainer.apply {
            leagueAdapter.setData(tournaments)
            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val tournament = tournaments[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    if (tournament.id == HomeViewModel.TOURNAMENT_ALL_ID) {   //ALL 標籤
                        ivLeagueIcon.visibility = View.GONE
                        tvLeagueName.text = getString(R.string.league_all)
                    } else {
                        Glide.with(this@NewHomeFragment).load(tournament.icon).into(ivLeagueIcon)
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
                    10f.dp2px,
                    0
                )
                tab.view.setOnClickListener {
                    //傳聯賽id索取賽事列表資料更新列表
                }
            }.attach()
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setCurrentPlayType(PlayType.TODAY)
    }

    override fun initListener() {
        with(mBinding) {
            llWalletEntry.setOnClickListener {

            }

            llFavoriteEntry.setOnClickListener {

            }

            llSearchEntry.setOnClickListener {

            }

            llBetEntry.setOnClickListener {

            }
        }
    }

    override fun createObserver() {
        mViewModel.sportsStatistical.observe(viewLifecycleOwner) {
            mViewModel.setCurrentSport(it[0].id)
            sportsListAdapter.setData(it)
            sportsListAdapter.notifyItemRangeChanged(0, it.size - 1)
        }

        mViewModel.tournaments.observe(viewLifecycleOwner) {
            initLeaguesLayout(it)
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.tvWalletBalance.text = it.getFormalMoney()
        }
    }
}