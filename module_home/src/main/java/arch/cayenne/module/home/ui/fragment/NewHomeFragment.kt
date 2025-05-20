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
import arch.cayenne.lib.common.utils.ext.extractDate
import arch.cayenne.lib.common.utils.ext.toChineseMonth
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.databinding.HomeTourPopupCalendarViewBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.ui.view.HomeCalendarPopupWindow
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
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

    //    private var tournamentListFragment: TournamentListFragment? = null
    private var isExpanded = false

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
                            toggleTournamentMoreSection(true)
//                            navigate(NewHomeFragmentDirections.actionNewHomeFragmentToChampionFragment(matchId = 464046))
                        } else {
                            toggleTournamentMoreSection(false)
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
        mBinding.llTournamentsDropdown.visibility = View.GONE
        with(mBinding.layoutContainer) {
            tlDateList.visibility = View.GONE
            llOtherDate.visibility = View.GONE
        }
//        mBinding.ivHomeLeagueMore.isEnabled = true
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
        // 取得未來 30 天 (MMDD, 星期, timeStamp)
        val dateTabs = getFutureThirtyDays()
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
                        val dateTriple = getFutureThirtyDays()
                            .getOrNull(dateTabIndex - 1)
                        "選中日期,時間戳:$dateTriple".logd()
                        dateTriple?.third ?: 0L
                    }
                    mViewModel.setSelectedDate(dateTimestamp)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            mBinding.ivHomeLeagueMore.clickNoRepeat {
                "ivHomeLeagueMore click".logd()
                mViewModel.getAllTournament()
                toggleTournamentMoreSection(true)
            }
            // 其他日期 Tab 設定
            llOtherDate.clickNoRepeat {
                //呼叫日曆popup元件
                //TODO 傳入目前被選tab的日期
                var tabSelectedDate = "0"
                val index = mBinding.layoutContainer.tlDateList.selectedTabPosition
                if (index > 0) {
                    val endDateTriple = getFutureThirtyDays().getOrNull(index - 1)
                    tabSelectedDate = DateUtils.getDate(endDateTriple?.third ?: 0L, "yyyyMMdd")
                }
                showHomeCalendar(tabSelectedDate)
            }
        }
    }


    private fun toggleTournamentMoreSection(expanded: Boolean) {
        val tag = "tournament_dropdown"
        val fm = childFragmentManager
        val container = mBinding.llTournamentsDropdown
        isExpanded = expanded
        if (expanded) {
            if (fm.findFragmentByTag(tag) != null) return
            container.visibility = View.INVISIBLE

            val tournamentListFragment = TournamentListFragment.newInstance()

            container.post {
                container.visibility = View.VISIBLE
            }
            fm.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_top,
                    R.anim.slide_out_to_top
                )
                .replace(R.id.ll_tournaments_dropdown, tournamentListFragment, tag)
                .commitAllowingStateLoss()
        } else {
            val fragment = fm.findFragmentByTag(tag) ?: return

            fm.beginTransaction()
                .setCustomAnimations(0, R.anim.slide_out_to_top)
                .remove(fragment)
                .commitAllowingStateLoss()

            container.postDelayed({
                mBinding.ivHomeLeagueMore.visibility = View.VISIBLE
                container.visibility = View.GONE
            }, 200)
        }
    }

    private fun showHomeCalendar(tabSelectedDate: String) {
        //設定標記紅色日期及可選取日期範圍
        fun setSchemeDate(calendarView: CalendarView) {
            val map: MutableMap<String, Calendar> = HashMap()
            //設定可以標記為紅色字的日期區間，目前設定為30天
            for (date in getFutureThirtyDays()) {
                val dateArray = DateUtils.getDate(date.third, "yyyy-MM-dd").split("-")
                map[getSchemeCalendar(
                    dateArray[0].toInt(),
                    dateArray[1].toInt(),
                    dateArray[2].toInt()
                ).toString()] =
                    getSchemeCalendar(
                        dateArray[0].toInt(),
                        dateArray[1].toInt(),
                        dateArray[2].toInt()
                    )

            }
            val endDateTriple = getFutureThirtyDays()
                .getOrNull(mBinding.layoutContainer.tlDateList.tabCount - 2)
            val endDateArray =
                DateUtils.getDate(endDateTriple?.third ?: 0L, "yyyy-MM-dd").split("-")
            //設定可以選取的日期區間，目前設定為30天
            calendarView.setRange(
                calendarView.curYear,
                calendarView.curMonth,
                calendarView.curDay,
                endDateArray[0].toInt(),
                endDateArray[1].toInt(),
                endDateArray[2].toInt()
            )
            calendarView.setSchemeDate(map)
        }

        fun setCurrentDate(vb: HomeTourPopupCalendarViewBinding, tabSelectedDate: String) {
            val currentYear = vb.calendarView.curYear
            val currentMonth = vb.calendarView.curMonth
            //日期tab為全部時標記為今日
            if (tabSelectedDate == "0") {
                vb.calendarView.scrollToCurrent(true)
                vb.tvCurrentMonth.text = "${currentMonth.toChineseMonth()} $currentYear"
            } else {
                val result = tabSelectedDate.extractDate()
                result?.let {
                    val (year, month, day) = it
                    with(vb) {
                        calendarView.scrollToCalendar(year, month, day)
                        tvCurrentMonth.text = "${month.toChineseMonth()} $year"
                    }
                } ?: run {
                    val curYear = vb.calendarView.curYear
                    val curMonth = vb.calendarView.curMonth
                    vb.calendarView.scrollToCurrent(true)
                    vb.tvCurrentMonth.text = "${curMonth.toChineseMonth()} $curYear"
                }
            }

        }
        // 使用 Builder 創建 Popup
        val customPopup = HomeCalendarPopupWindow.Builder(
            requireContext(),
            HomeTourPopupCalendarViewBinding::inflate
        )
            .build()
        with(customPopup.binding) {
            // 獲取當前日期
            var selectedDate = if (tabSelectedDate == "0") {
                "${calendarView.selectedCalendar}"
            } else {
                tabSelectedDate
            }
            // 透過 binding 操作 Popup 內部的 View
            ivRightClick.clickNoRepeat {
                calendarView.scrollToNext(true)
            }
            ivLeftClick.clickNoRepeat {
                calendarView.scrollToPre(true)
            }
            calendarBtnCancel.clickNoRepeat {
                customPopup.dismiss() // 關閉 Popup
            }
            calendarBtnOk.clickNoRepeat {
                setSelectedDateTab(selectedDate)
                customPopup.dismiss()
            }
            setSchemeDate(calendarView)
            setCurrentDate(customPopup.binding, tabSelectedDate)
            calendarView.setOnCalendarSelectListener(object :
                CalendarView.OnCalendarSelectListener {
                override fun onCalendarOutOfRange(calendar: Calendar?) {

                }

                override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                    if (calendar == null) return
                    selectedDate = "$calendar"
                    tvCurrentMonth.text =
                        "${calendar.month.toChineseMonth()} ${calendar.year}"
                    //控制左右按鈕的enabled
                    if (calendar.month > calendarView.curMonth) {
                        ivRightClick.isEnabled = false
                        ivLeftClick.isEnabled = true
                    } else {
                        ivRightClick.isEnabled = true
                        ivLeftClick.isEnabled = false
                    }
                }
            })
        }
        // 顯示 Popup
        customPopup.showAsDropDown(mBinding.layoutContainer.tlDateList)
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun setSelectedDateTab(selectedDate: String) {
        with(mBinding.layoutContainer) {
            val dateIndex = getFutureThirtyDays().indexOfFirst {
                DateUtils.getDate(it.third, "yyyyMMdd") == selectedDate
            } + 1
            tlDateList.getTabAt(dateIndex)?.select()
        }
    }

    private fun getFutureThirtyDays() = DateUtils.getFutureDays(30, Locale.getDefault())
    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.drawIndex = 0
        return calendar
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
        "setTournamentAndViewPagerLayout: ${tournaments.size}".logd()
        //確定拿到聯賽資料後再決定要不要show出時間
        if (tournaments.isNotEmpty()) {
            with(mBinding.layoutContainer) {
                if (mViewModel.getCurrentPlayType() == PlayType.TODAY) {
                    tlDateList.visibility = View.GONE
                    llOtherDate.visibility = View.GONE
                } else if (mViewModel.getCurrentPlayType() == PlayType.EARLY) {
                    tlDateList.visibility = View.VISIBLE
                    llOtherDate.visibility = View.VISIBLE
                }
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
            setTournamentAndViewPagerLayout(it)
        }

        mViewModel.selectedTournamentId.observe(viewLifecycleOwner) { id ->
            "使用者選取聯賽 ID: $id".logd()
            val list = mViewModel.tournaments.value.orEmpty()
            "使用者選取聯賽 tournaments: ${list.size}".logd()
            val index = list.indexOfFirst { it.id == id }
            if (index != -1) {
                "使用者選取聯賽 index select(): $index".logd()
                mBinding.layoutContainer.tlLeagueList.post {
                    mBinding.layoutContainer.tlLeagueList.getTabAt(index)?.select()
                }
            }
        }

        mViewModel.collapseTournamentDropdown.observe(viewLifecycleOwner) { shouldCollapse ->
            if (shouldCollapse == true && isExpanded) {
                toggleTournamentMoreSection(false)
//                isExpanded = false
                mViewModel.consumeCollapseTournamentDropdown() // 重置事件，避免重複觸發
            }
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.tvWalletBalance.text = it.getFormalMoney()
        }
    }
}