package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private var drawerContentFragment: DrawerContentFragment? = null
    private val sportsListAdapter by lazy {
        SportsListAdapter { sport ->
            Toast.makeText(
                requireContext(),
                "選擇：${getString(SportType.fromId(sport.id)!!.titleResId)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
//    private lateinit var leagueAdapter: LeaguePagerAdapter

    override fun initView(savedInstanceState: Bundle?) {

        childFragmentManager.beginTransaction()
            .replace(mBinding.floatingContainer.id, FloatingButtonFragment())
            .commit()
        initPlayTypeLayout()
        initSportLayout()
        initTournamentLayout()
        initDrawerContent()
    }

    //init 一級導航欄位
    private fun initPlayTypeLayout() {
        with(mBinding) {
            ivHomeSidebar.clickNoRepeat {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            PlayType.entries.forEach {
                tlHome.addTab(tlHome.newTab().setText(it.titleRes))
            }
            tlHome.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.apply {
                        //看db, 點擊的不在matchBean中會爆掉
                        resetHomeView()
                        mViewModel.setCurrentPlayType(PlayType.entries[this])
                        if (mViewModel.getCurrentPlayType() == PlayType.CHAMPION) {
                            navigate(NewHomeFragmentDirections.actionNewHomeFragmentToChampionFragment(matchId = 464046))
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })


        }
    }

    //當一級導航改變時，先把底下的view資料清除，等待讀取最新的資料，避免api取得過久，導致UI不協調
    private fun resetHomeView() {
        mBinding.layoutContainer.tlDateList.visibility = View.GONE
        mViewModel.resetLiveData()
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
        // 取得未來 7 天 (MMDD, 星期, timeStamp)
        val dateTabs = DateUtils.getFutureDays(7, Locale.getDefault())
        with(mBinding.layoutContainer) {
            //聯賽
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null

            // 日期 Tab 設定
            updateDateTabs(tlDateList, dateTabs)
            tlDateList.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val dateTabIndex = tab?.position ?: 0
                    val dateTimestamp: Long = if (dateTabIndex == 0) {
                        0L // 代表「全部」
                    } else {
                        val dateTriple = DateUtils.getFutureDays(7, Locale.getDefault())
                            .getOrNull(dateTabIndex - 1)
                        "選中日期,時間戳:$dateTriple".logd()
                        dateTriple?.third ?: 0L
                    }
                    mViewModel.setSelectedDate(dateTimestamp)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    //init DrawerLayout Content
    private fun initDrawerContent() {
        //蒙層顏色依照版型作變化
        mBinding.drawerLayout.setScrimColor(
            SkinnableResourceManager.getColor(
                requireContext(),
                R.color.drawer_scrim_color
            )
        )
        if (drawerContentFragment == null) {
            drawerContentFragment = DrawerContentFragment()
        }
        childFragmentManager.beginTransaction()
            .replace(
                mBinding.fragmentDrawerContent.id,
                drawerContentFragment!!,
                DrawerContentFragment.TAG
            )
            .commitNow()
    }

    private fun updateDateTabs(
        tlDateList: TabLayout,
        dateTabs: List<Triple<String, String, Long>>
    ) {
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
            dateTabs.forEach { (date, weekday, _) -> addTab(createTab(date, weekday)) }

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

    private fun setTournamentAndViewPagerLayout(tournaments: List<TournamentDataModel>) {
        //確定拿到聯賽資料後再決定要不要show出時間
        if (tournaments.isNotEmpty()) {
            if (mViewModel.getCurrentPlayType() == PlayType.TODAY) {
                mBinding.layoutContainer.tlDateList.visibility = View.GONE
            } else if (mViewModel.getCurrentPlayType() == PlayType.EARLY) {
                mBinding.layoutContainer.tlDateList.visibility = View.VISIBLE
            }
        }
        mBinding.layoutContainer.apply {
            vpGameList.currentItem = 0
            tlDateList.getTabAt(0)?.select()
            vpGameList.adapter = LeaguePagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                tournament = tournaments,
                playType = mViewModel.getCurrentPlayType()

            )
            TabLayoutMediator(tlLeagueList, vpGameList) { tab, position ->
                val tournament = tournaments[position]

                val tabBinding =
                    ItemLeagueTabBinding.inflate(LayoutInflater.from(context), null, false)
                tabBinding.apply {
                    if (tournament.id == HomeViewModel.TOURNAMENT_ALL_ID) {   //ALL 標籤
                        ivLeagueIcon.visibility = View.GONE
                        tvLeagueName.text = getString(R.string.league_all)
                    } else {
                        Glide.with(this@NewHomeFragment)
                            .load(tournament.icon.ifEmpty { R.drawable.ic_default_tournament })
                            .placeholder(R.drawable.ic_default_tournament)
                            .error(R.drawable.ic_default_tournament)
                            .into(ivLeagueIcon)
                        tvLeagueName.text = tournament.simpleName
                        ivLeagueIcon.imageTintList = context?.let {
                            SkinnableResourceManager.getColorStateList(
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
            }.attach()

            tlLeagueList.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.isSelected = true
                    vpGameList.currentItem = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setCurrentPlayType(PlayType.TODAY)
    }

    override fun initListener() {
        with(mBinding) {
            llWalletEntry.setOnClickListener {
                navigate(R.id.homeFragment)
            }

            llFavoriteEntry.setOnClickListener {

            }

            llSearchEntry.setOnClickListener {
                navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink())
            }

            llBetEntry.setOnClickListener {
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToHomeBetSlipFragment())
            }
        }
    }

    override fun createObserver() {
        mViewModel.sportsStatistical.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mViewModel.setCurrentSport(it[0].id)
            }
            sportsListAdapter.setData(it)
            sportsListAdapter.notifyItemRangeChanged(0, it.size - 1)
        }

        mViewModel.tournaments.observe(viewLifecycleOwner) {
//            if (it.isNullOrEmpty()) return@observe
            setTournamentAndViewPagerLayout(it)
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.tvWalletBalance.text = it.getFormalMoney()
        }
    }
}