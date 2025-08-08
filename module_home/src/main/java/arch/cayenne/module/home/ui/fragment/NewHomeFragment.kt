package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.reflexMargin
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.setupEndTabMoreAnimation
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.lib.common.utils.helper.BounceEdgeEffectHelper
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.ui.view.CustomTabLayoutMediator
import arch.cayenne.module.home.ui.view.HomeCalendarFragment
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private var drawerContentFragment: DrawerContentFragment? = null
    private val sportsListAdapter by lazy {
        SportsListAdapter { id ->
            if (mViewModel.currentSportId == id) return@SportsListAdapter
            mViewModel.setCurrentSport(id)
        }
    }
    private var customPopup : HomeCalendarFragment? = null
    private var tournamentTabLayoutMediator: CustomTabLayoutMediator? = null

    private var leaguePagerAdapter : LeaguePagerAdapter? = null
    private var gameListPageCallback: OnPageChangeCallback? = null

    //    private val tournamentListFragment  = TournamentListFragment.newInstance()
    private var isExpanded = false
    override fun initView(savedInstanceState: Bundle?) {
        initPlayTypeLayout()
        initSportLayout()
        initTournamentLayout()
        initDrawerContent()
        (mBinding.rvSportsList.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.clMain)
        super.onStart()
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
                        mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                        mViewModel.setCurrentPlayType(PlayType.entries[this].id)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    //當一級導航改變時，先把底下的view資料清除，等待讀取最新的資料，避免api取得過久，導致UI不協調
    private fun resetHomeView() {
        toggleTournamentMoreSection(false, TournamentListType.NONE)
        mBinding.layoutContainer.llDateFilterContainer.visibility = View.GONE
        mBinding.layoutContainer.llOtherDate.visibility = View.GONE
        mBinding.ivTournamentMore.visibility = View.GONE
    }

    //init 二級導航欄位
    private fun initSportLayout() {
        mBinding.apply {
            rvSportsList.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                edgeEffectFactory = BounceEdgeEffectHelper(requireContext())
                overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
                isNestedScrollingEnabled = false
                adapter = sportsListAdapter
            }
        }
    }

    //init 三級導航欄位與日期
    @SuppressLint("DefaultLocale")
    private fun initTournamentLayout() {
        // 取得未來 31 天 (MMDD, 星期, timeStamp)
        val dateTabs = getFutureSevenDays()
        with(mBinding.layoutContainer) {
            //聯賽
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null
            //如果往右往左滑動，等待滑動完成後，再去開始startObserveMatch
            gameListPageCallback = object : OnPageChangeCallback(){
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == SCROLL_STATE_IDLE) {
                        val itemId = leaguePagerAdapter?.getItemId(vpGameList.currentItem)?: return
                        val fragment = childFragmentManager.findFragmentByTag("f$itemId") ?: return
                        if (fragment is MatchListPagerFragment) {
                            fragment.startObserveMatch()
                        }
                    }
                }
            }
            vpGameList.registerOnPageChangeCallback(gameListPageCallback!!)
            //如果直接點擊聯賽到ViewPager還沒生成的MatchListPageFragment聯賽的話，這個MatchListPageFragment會生成並且attach上去，所以在這裡需要做attach完成後的startObserveMatch
            childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    val currentItemId = "f${leaguePagerAdapter?.getItemId(vpGameList.currentItem)}"
                    if (f is MatchListPagerFragment && f.tag == currentItemId) {
                        f.startObserveMatch()
                    }
                }
            }, false)

            // 日期 Tab 設定, 固定 "全部"
            updateDateTabs(tlDateList, dateTabs)
            addDateTabListener()

            tvTabAll.clickNoRepeat {
                resetDateTabs()
                mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
            }

            // 其他日期 Tab 設定
            llOtherDate.clickNoRepeat {
                // 轉換日期格式為 YYYYMMDD 給 DatePicker 使用
                fun List<String>.toYYYYMMDD(): String {
                    val year = this[0]
                    val month = this[1].padStart(2, '0')
                    val day = this[2].padStart(2, '0')
                    return "$year$month$day"
                }

                //呼叫日曆popup元件
                val tabSelectedDate = if (tlDateList.selectedTabPosition >= 0) {
                    tlDateList.getTabAt(tlDateList.selectedTabPosition)?.let { tab ->
                        getFuture31Days().find { it.first == tab.tag }?.let { triple ->
                            tab.view.isSelected = false
                            triple.third.getFormatDate().split("/").toYYYYMMDD()
                        } ?: "0"
                    } ?: "0"
                } else {
                    tvTabAll.isSelected = false
                    "0"
                }
                showHomeCalendar(tabSelectedDate)
            }

            // 初始化 TabLayout end more跟手動畫
            tlLeagueList.setupEndTabMoreAnimation(
                mBinding.ivTournamentMore,
                mBinding.llHomeTournamentMore,
                triggerRatio = 0.8f
            )
        }

        mBinding.ivTournamentMore.apply {addScaleOnTouchAnimation()}.clickNoRepeat {
            toggleTournamentMoreSection(true, TournamentListType.MORE)
        }
        mBinding.llHomeTournamentMore.clickNoRepeat {
            toggleTournamentMoreSection(true, TournamentListType.MORE)
        }
    }

    private fun addDateTabListener() {
        mBinding.layoutContainer.tlDateList.addOnTabSelectedListener(object :
            OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mBinding.layoutContainer.tvTabAll.isSelected = false

                tab?.tag?.apply {
                    val dateTimestamp = getFuture31Days().find { it.first == this }?.third ?: return
                    lifecycleScope.launch {
                        mViewModel.selectedDate(dateTimestamp)
                        mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun clearDateTabSelection() {
        val tabLayout = mBinding.layoutContainer.tlDateList
        tabLayout.setScrollPosition(0, 0f, true)
        val tabStrip = tabLayout.getChildAt(0) as? LinearLayout ?: return
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i)?.isSelected = false
            tabLayout.getTabAt(i)?.customView?.isSelected = false
        }
        tabLayout.selectTab(null)
    }

    private fun resetDateTabs() {
        mBinding.layoutContainer.tvTabAll.isSelected = true
        clearDateTabSelection()
        lifecycleScope.launch {
            mViewModel.selectedDate(0L)
        }

    }

    /***
     * @param expanded : Boolean 展開、收起
     * @param type : TournamentListType 是屬於今日和早盤的展開型聯賽列表或是屬於冠軍型的聯賽列表，
     * 或是none，表示切換到其他一級導航前先把目前的聯賽列表馬上收起來，例如 今日 -> 冠軍 or 冠軍 -> 今日，這種情況下一律沒有動畫
     *
     * */
    private fun toggleTournamentMoreSection(
        expanded: Boolean,
        type: TournamentListType
    ) {
        val tag = "tournament_dropdown"
        val fm = childFragmentManager
        val container = mBinding.llTournamentsDropdown
        isExpanded = expanded
        if (expanded) {
            if (fm.findFragmentByTag(tag) != null) return
            container.visibility = View.VISIBLE

            val tournamentListFragment = TournamentListFragment.newInstance(mViewModel.currentPlayTypeId, mViewModel.currentSportId, type)

            fm.beginTransaction().apply {
                if (type == TournamentListType.MORE) {
                    setCustomAnimations(
                        R.anim.slide_in_from_top,
                        R.anim.slide_out_to_top
                    )
                }
                replace(R.id.ll_tournaments_dropdown, tournamentListFragment, tag)
                commitAllowingStateLoss()
            }

        } else {
            val fragment = fm.findFragmentByTag(tag) ?: return
            fm.beginTransaction().apply {
                if (type == TournamentListType.MORE) {
                    setCustomAnimations(0, R.anim.slide_out_to_top)
                }
                remove(fragment)
                commitNow()
            }
        }
    }

    private fun showHomeCalendar(tabSelectedDate: String) {
        with(mBinding.layoutContainer) {
            llOtherDate.isSelected = true

            customPopup = HomeCalendarFragment.Builder().apply {
                val statusBarHeight =
                    ViewCompat.getRootWindowInsets(requireView())
                        ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
                val marginTopHeight = mBinding.clSecondNavbar.bottom + llDateFilterContainer.bottom - statusBarHeight
                setMarginTop(marginTopHeight)
                setMaskView(mBinding.viewCalendarMask)
                // 取得 maskView 的 LayoutParams
                val params = mBinding.viewCalendarMask.layoutParams as ViewGroup.MarginLayoutParams
                // 設定 topMargin
                params.topMargin = marginTopHeight
                // 將修改後的 LayoutParams 重新應用到 maskView
                mBinding.viewCalendarMask.layoutParams = params
                mViewModel.recently7DayMatchScheduleCount.value?.peekContent()?.let { setRange(it) }
                setOnDateSelectedListener { selectedDate ->
                    setSelectedDateTab(getFuture31Days().find { it.first == selectedDate })
                }
                setOnResetDateListener {
                    resetDateTabs()
                }
                setOnDismissListener {
                    llOtherDate.isSelected = false
                    customPopup = null

                    // 重置日期tab選擇狀態
                    tlDateList.getTabAt(tlDateList.selectedTabPosition)?.let {
                        if(!it.view.isSelected) {
                            it.view.isSelected = true
                        }
                    } ?: run { tvTabAll.isSelected = true }
                }
            }.build()

            customPopup?.show(childFragmentManager, mBinding.clMain.id, tabSelectedDate)
        }
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun setSelectedDateTab(dateTriple: Triple<String, String, Long>?) {
        with(mBinding.layoutContainer) {
            if (dateTriple == null) {
                resetDateTabs()
                return
            }
            var indexOfTabs = -1
            for( i in 0 until tlDateList.tabCount) {   //尋找是否在目前的tab內已經存在，存在的話跳到該tab就好
                val tab = tlDateList.getTabAt(i)
                if (tab?.tag == dateTriple.first){
                    indexOfTabs = i
                    break
                }
            }
            if (indexOfTabs != -1) {
                tlDateList.getTabAt(indexOfTabs)?.select()
            } else {
                tlDateList.addTab(createDateTab(dateTriple.first, dateTriple.second), true)
                setupDateTabLayoutParams(tlDateList, false)
            }
        }
    }

    private fun setupDateTabLayoutParams(tabLayout: TabLayout, clearSelected: Boolean) {
        tabLayout.post {
            val tabStrip = tabLayout.getChildAt(0) as LinearLayout
            for (i in 0 until tabStrip.childCount) {
                tabStrip.getChildAt(i).apply {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.width = 56.dp2px
                    params.height = 50.dp2px
                    params.marginStart = 5.dp2px
                    layoutParams = params
                    setBackgroundResource(R.drawable.selector_date_tab_bg)
                    if (clearSelected) isSelected = false
                }
            }
        }

    }
    private fun getFutureSevenDays() = getFuture31Days().take(7)
    private fun getFuture31Days() = DateUtils.getFutureDays(31, Locale.getDefault())

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
            drawerContentFragment?.also {
                it.setOnFunctionClickListener {
//                    mBinding.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        }
        childFragmentManager.beginTransaction()
            .replace(
                mBinding.fragmentDrawerContent.id,
                drawerContentFragment!!,
                DrawerContentFragment.TAG
            )
            .commitNow()
        //如果由模拟投注页面跳转到首页需要关闭左侧菜单栏
        observeResult<String>("Drawer") {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START,false)
        }
    }

    private fun updateDateTabs(
        tlDateList: TabLayout,
        dateTabs: List<Triple<String, String, Long>>
    ) {
        tlDateList.apply {
            removeAllTabs()
            dateTabs.forEach { (date, weekday, _) ->
                val tab = createDateTab(date, weekday)
                addTab(tab)
            }
            setupDateTabLayoutParams(tlDateList, true)
        }
    }

    private fun createDateTab(date: String?, weekday: String?): TabLayout.Tab {
        val tab = mBinding.layoutContainer.tlDateList.newTab()
        val tabView = ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false).apply {
            tvDate.text = date
            tvWeekDay.visibility = View.VISIBLE
            tvWeekDay.text = weekday
        }
        tab.customView = tabView.root
        tab.tag = date
        return tab
    }

    private fun setTournamentAndViewPagerLayout(tournaments: List<TournamentDataModel>) {
        with(mBinding.layoutContainer) {
            leaguePagerAdapter = LeaguePagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                tournament = tournaments,
                playTypeId = mViewModel.currentPlayTypeId
            )
            vpGameList.adapter = leaguePagerAdapter
            vpGameList.offsetLeftAndRight(1)

            // 使用 reflexMargin 擴展方法設置更小的 tab 間距
            tlLeagueList.reflexMargin(2.dp2px, 2.dp2px, 1.dp2px)

            //因為一開始有觸發resetHome(),觸發resetLiveData()，所以observe livedata tournaments可能會是空的
            //導致tabLayout沒有資料時又多設定一次OnTabSelectedListener，因此要先清除之前的listener
            mBinding.layoutContainer.tlLeagueList.clearOnTabSelectedListeners()

            tournamentTabLayoutMediator?.detach()
            tournamentTabLayoutMediator = CustomTabLayoutMediator(
                tabLayout = tlLeagueList,
                viewPager = vpGameList
            ) { tab, position ->
                tournaments.getOrNull(position)?.let {
                    tab.customView = createTournamentTabView(it)
                    tab.view.setPadding(0, 0, 10f.dp2px, 0)
                }
            }.also { layoutMediator ->
                layoutMediator.attach(
                    afterTabSelected = { position ->
                        getSelectedRecently31Scheduled(position)
                        tournaments.getOrNull(position)?.id?.let { id -> mViewModel.setCurrentTournamentId(id)}
                        mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                    }
                )
                val selectedPosition = tournaments.indexOfFirst { it.isSelected }
                getSelectedRecently31Scheduled(selectedPosition)
                tlLeagueList.post{ layoutMediator.selectTabWithoutAnimation(selectedPosition) }
                vpGameList.post { gameListPageCallback?.onPageScrollStateChanged(SCROLL_STATE_IDLE) }
            }
        }
    }

    private fun getSelectedRecently31Scheduled(selectedIndex: Int) {
        if (mViewModel.currentPlayTypeId == PlayType.EARLY.id) {
            val list = mViewModel.tournaments.value?.peekContent().orEmpty()
            if (list.isEmpty()) return
            mViewModel.getRecently31MatchScheduleCount(list[selectedIndex].id)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setCurrentPlayType(PlayType.TODAY.id)
    }

    override fun initListener() {
        with(mBinding) {
            llWalletEntry.apply {
                addScaleOnTouchAnimation(ivWalletAdd)
            }.setOnClickListener {
                mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                //navigate(Uri.parse("walisport://module_home/homeFragment"))
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }
            llFavoriteEntry.setOnClickListener {
                mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToCollectListFragment())
            }
            llFavoriteEntry.addScaleOnTouchAnimation()
            llSearchEntry.setOnClickListener {
                mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink())
            }
            llSearchEntry.addScaleOnTouchAnimation()
            llBetEntry.setOnClickListener {
                mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToHomeBetSlipFragment())
            }
            llBetEntry.addScaleOnTouchAnimation()
        }
    }

    override suspend fun createObserver() {
        mViewModel.sportsStatistical.observeEvent(viewLifecycleOwner, this) {
            sportsListAdapter.submitList(it)
        }

        mViewModel.tournaments.observeEvent(viewLifecycleOwner, this) { list ->
            setTournamentAndViewPagerLayout(list)
        }

        mViewModel.collapseTournamentDropdown.observeEvent(viewLifecycleOwner, this) { shouldCollapse ->
            if (shouldCollapse && isExpanded) {
                toggleTournamentMoreSection(false, TournamentListType.MORE)
                mViewModel.consumeCollapseTournamentDropdown() // 重置事件，避免重複觸發
            }
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.tvWalletBalance.text =
                getString(
                    R.string.balance_format,
                    CurrencySymbols.getSymbol(it?.currency?:""),
                    (it?.balance?:0L).getFormalMoney()
                )
        }

        mViewModel.navigationToChampion.observeEvent(viewLifecycleOwner, this) { data ->
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.newHomeFragment) {
                navigate(
                    NewHomeFragmentDirections.actionNewHomeFragmentToChampionFragment(
                        matchId = data.championMatchId,
                        name = data.name,
                        icon = data.icon
                    )
                )
            }
        }
        mViewModel.recently7DayMatchScheduleCount.observeEvent(viewLifecycleOwner, this) { list->
            customPopup?.updateRange(list)
        }
        mViewModel.selectedDate.observeEvent(viewLifecycleOwner, this) { select ->
            if (select == HomeViewModel.DEFAULT_DATE) return@observeEvent
            setSelectedDateTab(getFuture31Days().find { it.third == select })
        }
        mViewModel.playTypeIndexChange.observeEvent(viewLifecycleOwner, this) {
            mBinding.tlHome.getTabAt(it)?.select()
            resetHomeView()
            mBinding.groupHomeMain.visibility = View.VISIBLE
        }
        mViewModel.notifyToChampion.observeEvent(viewLifecycleOwner, this) {
            toggleTournamentMoreSection(true, TournamentListType.CHAMPION)
        }
        mViewModel.tournamentSlideOutEnd.observeEvent(viewLifecycleOwner, this) {
            mBinding.ivTournamentMore.visibility = View.VISIBLE
            mBinding.llTournamentsDropdown.visibility = View.GONE
        }

        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when(it) {
                is HomeState.Tournament.LoadSuccess, HomeState.Tournament.LoadFailure, HomeState.Sport.LoadFailure -> {
                    if (mViewModel.currentPlayTypeId == PlayType.EARLY.id) {
                        mBinding.layoutContainer.llDateFilterContainer.visibility = View.VISIBLE
                        mBinding.layoutContainer.llOtherDate.visibility = View.VISIBLE
                    }
                }
                else -> Unit
            }
        }
        mViewModel.calendarStates.observe(viewLifecycleOwner) {
            when (it) {
                HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING -> {
                    if (customPopup != null) {
                        customPopup?.callDismiss()
                    }
                }
                else -> Unit
            }
        }
    }

    private fun createTournamentTabView(
        tournament: TournamentDataModel
    ): View {
        val tabBinding =
            ItemLeagueTabBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        tabBinding.apply {
            tvLeagueName.text = if (tournament.id == HomeViewModel.TOURNAMENT_ALL_ID)
                getString(R.string.league_all)
            else tournament.simpleName

            if (tournament.id == HomeViewModel.TOURNAMENT_ALL_ID) {
                ivLeagueIcon.visibility = View.GONE
            } else {
                Glide.with(this@NewHomeFragment)
                    .load(tournament.icon.ifEmpty { R.drawable.ic_default_tournament })
                    .placeholder(R.drawable.ic_default_tournament)
                    .error(R.drawable.ic_default_tournament)
                    .into(ivLeagueIcon)
            }
            root.setBackgroundResource(R.drawable.selector_league_tab_bg)
        }
        return tabBinding.root
    }

    override fun onBackPressed(): Boolean {
        //如果抽屉打开，截获此次返回事件，关闭抽屉
        if(mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        if(isExpanded && mViewModel.currentPlayTypeId != PlayType.CHAMPION.id){
            toggleTournamentMoreSection(false, TournamentListType.MORE)
            return true
        }
        return super.onBackPressed()
    }

}