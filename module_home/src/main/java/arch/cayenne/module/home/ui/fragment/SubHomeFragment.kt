package arch.cayenne.module.home.ui.fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.reflexMargin
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.setupEndTabMoreAnimation
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.BounceEdgeEffectHelper
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.databinding.FragmentSubHomeBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.ui.view.CustomTabLayoutMediator
import arch.cayenne.module.home.ui.view.HomeCalendarFragment
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.SubHomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.KClass

class SubHomeFragment: BaseFragment<SubHomeViewModel, FragmentSubHomeBinding>() {
    override val vbClass: KClass<FragmentSubHomeBinding> = FragmentSubHomeBinding::class
    override val vmClass: KClass<SubHomeViewModel> = SubHomeViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()

    private var customPopup : HomeCalendarFragment? = null
    private var tournamentTabLayoutMediator: CustomTabLayoutMediator? = null

    private var leaguePagerAdapter : LeaguePagerAdapter? = null
    private var gameListPageCallback: ViewPager2.OnPageChangeCallback? = null

    private var isExpanded = false

    private val defaultAnimDuration = 300L

    private val sportsListAdapter by lazy {
        SportsListAdapter { id ->
            if (mViewModel.currentSportId == id) return@SportsListAdapter
            mViewModel.setCurrentSport(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.apply {
            mViewModel.setPlayTypeId(this.getInt(ARG_PLAY_TYPE_ID))
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun initView(savedInstanceState: Bundle?) {
        initSportLayout()
        if (mViewModel.currentPlayTypeId == PlayType.CHAMPION.id) {
            initChampionTournamentLayout()
        } else {
            initTournamentLayout()
        }
        if (mViewModel.currentPlayTypeId == PlayType.EARLY.id) {
            mBinding.layoutContainer.llDateFilterContainer.visibility = View.VISIBLE
            mBinding.layoutContainer.llOtherDate.visibility = View.VISIBLE
        }
    }

    private fun setMaskViewAlpha(visible: Boolean) {
        mBinding.vTournamentListMask.let {
            it.post {
                it.animate()
                    .alpha(if (visible) 1f else 0f)
                    .setDuration(defaultAnimDuration)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {
                            if (visible) it.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            if (!visible) it.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator) = Unit
                        override fun onAnimationRepeat(p0: Animator) = Unit
                    })
                    .start()
            }
        }
    }

    override fun initListener() {
        with(mBinding) {
            setTopMaskListener()
            llFavoriteEntry.setOnClickListener {
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToCollectListFragment())
            }
            llFavoriteEntry.addScaleOnTouchAnimation()
            llSearchEntry.setOnClickListener {
                navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink())
            }
            llSearchEntry.addScaleOnTouchAnimation()
            llBetEntry.clickNoRepeatSingle {
                //navigate(Uri.parse("walisport://module_home/homeFragment"))
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }
            llFavoriteEntry.clickNoRepeatSingle {
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToCollectListFragment())
            }
            llFavoriteEntry.addScaleOnTouchAnimation()
            llSearchEntry.clickNoRepeatSingle {
                navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink())
            }
            llSearchEntry.addScaleOnTouchAnimation()
            llBetEntry.clickNoRepeatSingle {
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToHomeBetSlipFragment())
            }
            llBetEntry.addScaleOnTouchAnimation()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override suspend fun createObserver() {
        mViewModel.currentSportIdChange.observeEvent(viewLifecycleOwner, this) {
            if (mViewModel.currentPlayTypeId != PlayType.CHAMPION.id) return@observeEvent
            (childFragmentManager.findFragmentByTag(PlayType.CHAMPION.name) as? TournamentListFragment)?.changeSportId(it)
        }

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

        mViewModel.tournamentSlideOutEnd.observeEvent(viewLifecycleOwner, this) {
            mBinding.ivTournamentMore.visibility = View.VISIBLE
            mBinding.llTournamentsDropdown.visibility = View.GONE
        }

        mViewModel.calendarStates.observe(viewLifecycleOwner) {
            when (it) {
                HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING -> {
                    if (customPopup != null &&
                        customPopup?.getAnimState() != HomeCalendarFragment.AnimState.COLLAPSING) {
                        customPopup?.callDismiss()
                    }
                }
                else -> Unit
            }
        }

        homeViewModel.languageManager.languageFlow.collect{
            //sportAdapter需要监听切换语言更新
            sportsListAdapter.notifyDataSetChanged()
        }

        homeViewModel.notifySubHomeRefresh.observeEvent(viewLifecycleOwner, this) {
            mViewModel.getCurrentSportStatistical()
            mViewModel.getCurrentTournament()
        }
    }

    fun onFragmentSelected() {
        mViewModel.getCurrentSportStatistical()
        mViewModel.getCurrentTournament()
    }
    fun onFragmentUnSelected() {
        mViewModel.requestCollapseTournamentDropdown()
        mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
    }


    //init 二級導航欄位
    private fun initSportLayout() {
        mBinding.apply {
            rvSportsList.apply {
                itemAnimator = null
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                edgeEffectFactory = BounceEdgeEffectHelper(requireContext())
                overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
                isNestedScrollingEnabled = false
                adapter = sportsListAdapter
            }
        }
    }

    //init 三級導航欄位與日期，只有今日和早盤有
    @SuppressLint("DefaultLocale")
    private fun initTournamentLayout() {
        // 取得未來 31 天 (MMDD, 星期, timeStamp)
        val dateTabs = getFutureSevenDays()
        with(mBinding.layoutContainer) {
            //聯賽
            vpGameList.isSaveEnabled = false
            vpGameList.adapter = null
            //如果往右往左滑動，等待滑動完成後，再去開始startObserveMatch
            gameListPageCallback = object : ViewPager2.OnPageChangeCallback(){
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
            childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
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
                val targetTab = tlDateList.getTabAt(tlDateList.selectedTabPosition)
                val tabSelectedDate = if (targetTab != null) {
                    targetTab.run {
                        view.post { view.isSelected = false }
                        getFuture31Days().find { it.first == tag }?.third?.getFormatDate()
                            ?.split("/")?.toYYYYMMDD()
                            ?: "0"
                    }
                } else {
                    tvTabAll.post { tvTabAll.isSelected = false }
                    "0"
                }
                showHomeCalendar(tabSelectedDate)
            }

            // 初始化 TabLayout end more跟手動畫
            tlLeagueList.setupEndTabMoreAnimation(
                mBinding.ivTournamentMore,
                mBinding.llHomeTournamentMore
            )
        }

        mBinding.ivTournamentMore.apply {addScaleOnTouchAnimation()}.clickNoRepeat {
            mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
            toggleTournamentMoreSection(true, TournamentListType.MORE)
        }
        mBinding.llHomeTournamentMore.clickNoRepeat {
            mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
            toggleTournamentMoreSection(true, TournamentListType.MORE)
        }
    }

    private fun addDateTabListener() {
        mBinding.layoutContainer.tlDateList.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mBinding.layoutContainer.tvTabAll.isSelected = false

                tab?.tag?.apply {
                    val dateTimestamp = getFuture31Days().find { it.first == this }?.third ?: return
                    lifecycleScope.launch {
                        mViewModel.selectedDate(dateTimestamp)
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

            // 展開時顯示遮罩層
            setMaskViewAlpha(true)

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

            // 收回時隱藏遮罩層
            setMaskViewAlpha(false)
            
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

                val marginTopHeight = mBinding.clSecondNavbar.height + tlLeagueList.height + tlDateList.height
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
                setOnBeforeDismissAnimListener {
                    llOtherDate.isSelected = false

                    // 重置日期tab選擇狀態
                    tlDateList.getTabAt(tlDateList.selectedTabPosition)?.let {
                        if(!it.view.isSelected) {
                            it.view.isSelected = true
                        }
                    } ?: run { tvTabAll.isSelected = true }
                }
                setOnAfterDismissAnimListener {
                    customPopup = null
                    mBinding.llCalendar.visibility = View.GONE
                }
            }.build()
            mBinding.llCalendar.visibility = View.VISIBLE
            customPopup?.show(childFragmentManager, mBinding.llCalendar.id, tabSelectedDate)
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
            if (leaguePagerAdapter == null) {
                leaguePagerAdapter = LeaguePagerAdapter(
                    fragmentManager = childFragmentManager,
                    lifecycle = viewLifecycleOwner.lifecycle,
                )
                vpGameList.adapter = leaguePagerAdapter
                vpGameList.offsetLeftAndRight(1)
            }
            leaguePagerAdapter!!.setData(mViewModel.currentPlayTypeId, tournaments)
            mBinding.ivTournamentMore.visibility = View.VISIBLE

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
                    }
                )
                if (tournaments.isNotEmpty()) {
                    val selectedPosition = tournaments.indexOfFirst { it.isSelected }
                    tlLeagueList.setScrollPosition(selectedPosition, 0f, true)
                    tlLeagueList.post { layoutMediator.selectTabWithoutAnimation(selectedPosition) }
                    vpGameList.post { gameListPageCallback?.onPageScrollStateChanged(SCROLL_STATE_IDLE) }
                }
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


    @SuppressLint("ClickableViewAccessibility")
    private fun setTopMaskListener() {
        with (mBinding) {
            clSubMain.setOnChildClickedInterceptedListener { view ->
                when (view) {
                    clSecondNavbar -> {
                        mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                    }
                    else-> Unit
                }
            }
            with(layoutContainer) {
                viewContainerRoot.setOnChildClickedInterceptedListener { view ->
                    when(view) {
                        tlContainer, llDateFilterContainer,llOtherDate -> {
                            mViewModel.setCalendarState(HomeCalendarFragment.States.CALENDAR_CLOSE_NOTHING)
                        }
                        else -> Unit
                    }
                }
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
                Glide.with(this@SubHomeFragment)
                    .load(tournament.icon.ifEmpty { R.drawable.ic_default_tournament })
                    .placeholder(R.drawable.ic_default_tournament)
                    .error(R.drawable.ic_default_tournament)
                    .into(ivLeagueIcon)
            }
            root.setBackgroundResource(R.drawable.selector_league_tab_bg)
        }
        return tabBinding.root
    }

    private fun initChampionTournamentLayout() {
        val tournamentListFragment = TournamentListFragment.newInstance(
            playTypeId = PlayType.CHAMPION.id,
            sportId = mViewModel.currentSportId,
            type = TournamentListType.CHAMPION
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_champion_container, tournamentListFragment, PlayType.CHAMPION.name)
            .commit()

    }

    companion object {
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        fun newInstance(playTypeId: Int): SubHomeFragment {
            return SubHomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                }
            }
        }
    }

}