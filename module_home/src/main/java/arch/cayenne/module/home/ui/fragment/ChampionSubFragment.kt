package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import arch.cayenne.lib.common.ui.view.CustomTabLayoutMediator
import arch.cayenne.lib.common.ui.view.WLLinearGradientFontSpan
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.BounceEdgeEffectHelper
import arch.cayenne.lib.common.utils.helper.VIPResourceHelper
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.TournamentCombo
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.TournamentListType
import arch.cayenne.module.home.data.constants.MatchListSortType
import arch.cayenne.module.home.databinding.FragmentSubHomeBinding
import arch.cayenne.module.home.databinding.ItemLeagueTabBinding
import arch.cayenne.module.home.databinding.LayoutTournamentSortingMenuBinding
import arch.cayenne.module.home.ui.adapter.LeaguePagerAdapter
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.ui.viewmodel.ChampionSubViewModel
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.KClass

/**
 * 冠军tab的Fragment, 用在体育首页的子页面中
 */
class ChampionSubFragment : BaseFragment<ChampionSubViewModel, FragmentSubHomeBinding>(),
    ISubFragmentLifecycle {
    override val vbClass: KClass<FragmentSubHomeBinding> = FragmentSubHomeBinding::class
    override val vmClass: KClass<ChampionSubViewModel> = ChampionSubViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()

    private var tournamentTabLayoutMediator: CustomTabLayoutMediator? = null

    private var leaguePagerAdapter: LeaguePagerAdapter? = null
    private var gameListPageCallback: ViewPager2.OnPageChangeCallback? = null

    private var allTabCompleteObserveJob: Job? = null
    private var drawTournamentTabJob: Job? = null
    private var drawSportListJob: Job? = null

    private var isExpanded = false
    private var sortingMenuBinding: LayoutTournamentSortingMenuBinding? = null

    // 當前排序類型，預設為按熱門聯賽排序
    private var currentSortType = MatchListSortType.BY_HOT

    private val defaultAnimDuration = 300L

    private val actManager by lazy { requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    private val sportsListAdapter by lazy {
        SportsListAdapter { id ->
            if (mViewModel.currentSportId == id) return@SportsListAdapter

            // 執行淡入淡出動畫
            mBinding.layoutContainer.viewContainerRoot.startFadeAnim { onComplete ->
                mViewModel.setCurrentSport(id)
                onComplete.invoke()
            }
        }
    }

    // VIP 等級資源設置於 lib_common，統一使用 VIPResourceHelper 管理

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.apply {
            mViewModel.setPlayTypeId(this.getInt(ARG_PLAY_TYPE_ID))
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun initView(savedInstanceState: Bundle?) {
        initSportLayout()
        launch { initSportBanner() }
        initChampionTournamentLayout()
        // 初始化聯賽按鈕狀態
        updateTournamentButtonStyle(mViewModel.hasTournamentSelections())
    }

    override fun initListener() {
        with(mBinding) {
            setTopMaskListener()

            clVipInfo.apply {
                clickNoRepeatSingle { navigate(arch.cayenne.lib.res.R.string.nav_module_vip_fragment.deeplink()) }
                addScaleOnTouchAnimation()
            }
        }
    }

    private fun showTournamentListBottomSheet() {
        val tag = "tournament_bottom_sheet"
        if (childFragmentManager.findFragmentByTag(tag) != null) return

        TournamentListBottomSheetFragment
            .newInstance(
                mViewModel.currentPlayTypeId,
                mViewModel.currentSportId,
                TournamentListType.MORE
            )
            .show(childFragmentManager, tag)
    }

    @SuppressLint("NotifyDataSetChanged")
    override suspend fun createObserver() {
        mViewModel.currentSportIdChange.observeEvent(viewLifecycleOwner, this) {
            if (mViewModel.currentPlayTypeId != PlayType.CHAMPION.id) return@observeEvent
            (childFragmentManager.findFragmentByTag(PlayType.CHAMPION.name) as? TournamentListFragment)?.changeSportId(
                it
            )
        }

        with(mViewModel) {
            sportsStatistical.observeEvent(viewLifecycleOwner, this@ChampionSubFragment) {
                tempSportData = it
                if (mViewModel.isAllowTabLoad) {
                    drawSportList()
                }
            }
        }

        mViewModel.tournaments.observeEvent(viewLifecycleOwner, this) { list ->
            setTournamentAndViewPagerLayout(list)
        }

        mViewModel.collapseTournamentDropdown.observeEvent(
            viewLifecycleOwner,
            this
        ) { shouldCollapse ->
            if (shouldCollapse && isExpanded) {
                toggleTournamentSorting(false)
                mViewModel.consumeCollapseTournamentDropdown() // 重置事件，避免重複觸發
            }
        }

        mViewModel.navigationToChampion.observeEvent(viewLifecycleOwner, this) { data ->
            navigate(Uri.parse("walisport://module_home/championFragment?matchId=${data.championMatchId}&name=${data.name}&icon=${data.icon}"))
        }

        mViewModel.tournamentSlideOutEnd.observeEvent(viewLifecycleOwner, this) {
            mBinding.layoutContainer.llTournamentsDropdown.visibility = View.GONE
        }

        // 觀察聯賽按鈕選中狀態變化
        mViewModel.tournamentButtonHasSelection.observeEvent(
            viewLifecycleOwner,
            this
        ) { hasSelection ->
            updateTournamentButtonStyle(hasSelection)
            if (hasSelection) {
                mBinding.layoutContainer.vpGameList.setCurrentItem(
                    leaguePagerAdapter!!.itemCount - 1,
                    false
                )
            }
        }

        // 觀察是否需要清除 tlLeagueList 的選中狀態
        mViewModel.shouldClearLeagueListSelection.observeEvent(viewLifecycleOwner, this) {
            clearLeagueListSelection()
        }

        homeViewModel.notifySubHomeRefresh.observeEvent(viewLifecycleOwner, this) {
            sportsListAdapter.notifyDataSetChanged()
            mViewModel.getCurrentTournament()
        }

        homeViewModel.apiStateListener.observe(viewLifecycleOwner) {
            if (it is HomeState.FirstMatchListComplete && !mViewModel.isAllowTabLoad) {
                mViewModel.isAllowTabLoad = true
                handleAllTabLoaded()
            }
        }

        mViewModel.onVipListener.observe(viewLifecycleOwner) {
            if (it != null) {
                var percent = "0%"
                var progress = 0f
                val betScore = it.admittedBetScore.toFloat()
                val reqScore = it.requiredAdmittedBetScore.toFloat()
                if (reqScore > 0L && betScore > 0L) {
                    progress = (betScore / reqScore) * 100f
                    percent = String.format("%.2f", progress) + "%"
                }
                val cny = CurrencySymbols.getSymbol(it.ccy) +
                        CurrencySymbols.getFormatAmount(it.ccy, reqScore)
                val info = getString(arch.cayenne.lib.common.R.string.vip_level_need, cny)
                updateVIPInfo(
                    vipLevel = it.vipLevel,
                    vipStage = it.vipStage,
                    percent = percent,
                    levelUpInfo = info,
                    progress
                )
            }
        }
    }

    override fun onFragmentSelected() {
        mViewModel.getCurrentSportStatistical()
        mViewModel.getCurrentTournament()
        tournamentTabLayoutMediator?.scrollTabToCurrentPositionImmediately()
    }

    override fun reloadCurrentMatchListPagerFragment() {
        if (mViewModel.currentPlayTypeId == PlayType.CHAMPION.id) {
            val fragment = childFragmentManager.findFragmentByTag(PlayType.CHAMPION.name)
            (fragment as? TournamentListFragment)?.reloadAllData()
        } else {
            val itemId =
                leaguePagerAdapter?.getItemId(mBinding.layoutContainer.vpGameList.currentItem)
                    ?: return
            val fragment = childFragmentManager.findFragmentByTag("f$itemId") ?: return
            (fragment as? MatchListPagerFragment)?.reloadAllData()
        }
    }

    // 設置更多按鈕的顯示狀態
    override fun onFragmentUnSelected() {
        mViewModel.requestCollapseTournamentDropdown()
        // 收起排序選單
        if (isExpanded) {
            toggleTournamentSorting(false)
        }
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
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: android.graphics.Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val pos = parent.getChildAdapterPosition(view)
                        val last = (parent.adapter?.itemCount ?: 0) - 1
                        outRect.right = if (pos == last) 0 else 4.dp2px   // marginEnd
                    }
                })
                adapter = sportsListAdapter
            }
        }
    }

    // init Sport Banner 輪播區塊
    private suspend fun initSportBanner() {
        val mockBannerList = mViewModel.getBannerList()

        with(mBinding.includeSportBanner) {
            pbSportBanner.setTriggerListener {
                vpSportBanner.setLoopTime(50)
                vpSportBanner.isAutoLoop(true)
                vpSportBanner.start()
                vpSportBanner.postDelayed({
                    vpSportBanner.stop()                    // 停止自动轮播
                    vpSportBanner.isAutoLoop(false)         // 关闭自动轮播功能 // 可选：允许下次再次触发
                }, 50)
            }
            val adapter = BannerImageMatchAdapter(mockBannerList)
            vpSportBanner.setAdapter(adapter)
            vpSportBanner.setBannerRound(9.dp2px.toFloat())
            vpSportBanner.isAutoLoop(false)
            // 设置滑动时长丝滑,不影响曲线,
            vpSportBanner.setScrollTime(600)  // 0.6 秒
            vpSportBanner.setPageTransformer(CustomCurveTransformer())
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            mBinding.includeSportBanner.pbSportBanner.stopTriggerJob()
        } else {
            mBinding.includeSportBanner.pbSportBanner.resetTriggerJob()
        }
        super.onHiddenChanged(hidden)
    }

    // 更新 VIP 信息顯示
    private fun updateVIPInfo(
        vipLevel: Int,
        vipStage: Int,
        percent: String,
        levelUpInfo: String,
        progress: Float
    ) {
        val level = VIPResourceHelper.getVIPLevelFromInt(vipStage)
        with(mBinding) {
            // 設置背景 - 使用 VIPResourceHelper
            clVipInfo.background = VIPResourceHelper.getSportBackgroundResource(level)
            // 設置圖標 - 使用 VIPResourceHelper
            ivLevel.setImageResource(VIPResourceHelper.getIconResource(level))
            ivLevelName.setImageResource(VIPResourceHelper.getLevelNameResource(level))
            // 設置文字漸變效果 - 使用 VIPResourceHelper
            val levelStr = getString(arch.cayenne.lib.common.R.string.vip_level_format, vipLevel)
            val start = VIPResourceHelper.getShaderStartColor().getColor(requireContext())
            val end = VIPResourceHelper.getShaderEndColor(level).getColor(requireContext())
            val span = getGradientSpan(levelStr, start, end)
            tvLevel.setText(span, TextView.BufferType.SPANNABLE)
            tvPercent.text = percent
            val color = VIPResourceHelper.getProgressStartColor(level)
            vipProgress.setProgressColor(color)
            vipProgress.setProgress(progress)
            tvLevelUpInfo.text = levelUpInfo
        }
    }

    private fun getGradientSpan(
        content: String,
        startColor: Int,
        endColor: Int
    ): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(content)
        val span = WLLinearGradientFontSpan(startColor, endColor)
        spannableStringBuilder.setSpan(
            span,
            0,
            spannableStringBuilder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableStringBuilder
    }

    // 全部Tab的比賽列表載入完成後的處理
    private fun handleAllTabLoaded() {
        allTabCompleteObserveJob = null
        drawTournamentTab()
        drawSportList()
    }

    // 畫聯賽列表
    private fun drawTournamentTab() {
        drawTournamentTabJob?.cancel()
        drawTournamentTabJob = launch {
            val tabLayout = mBinding.layoutContainer.tlLeagueList
            for (i in 0 until tabLayout.tabCount) {
                val tab = tabLayout.getTabAt(i)
                val data = tab?.tag as? TournamentCombo

                if (tab != null && data != null && tab.customView == null) {
                    tab.customView = createTournamentTabView(data)
                    tab.view.setPadding(0, 0, 6.dp2px, 0)
                    if (data.tournamentList[0].id == HomeViewModel.TOURNAMENT_ALL_ID) {
                        tab.view.minimumWidth = 0
                    }
                }
            }
        }
    }

    // 畫球種列表
    private fun drawSportList() {
        mViewModel.tempSportData?.let {
            drawSportListJob?.cancel()
            drawSportListJob = launch {
                sportsListAdapter.submitList(it) {
                    mViewModel.tempSportData = null
                }
            }
        }
    }

    /**
     * 讓相對應的position的MatchListPagerFragment內的matchListChange livedata可以開始observe比賽列表
     * 也就是開始繪製Match List的RecyclerView
     * */
    fun startObservePageMatchListChange(position: Int) {
        val currentPosition = position
        val itemId = leaguePagerAdapter?.getItemId(currentPosition) ?: return
        val fragment = childFragmentManager.findFragmentByTag("f$itemId") ?: return
        if (fragment is MatchListPagerFragment) {
            fragment.startObserveMatchListChange()
        }
        //如果記憶體過低，就不做預載左右兩頁
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        if (actManager.isLowRamDevice || memInfo.availMem < LOW_MEMORY_THRESHOLD) return

        if (currentPosition - 1 >= 0) {
            leaguePagerAdapter?.getItemId(currentPosition - 1)?.also { preItemId ->
                childFragmentManager.findFragmentByTag("f$preItemId").also { preFragment ->
                    if (preFragment is MatchListPagerFragment) {
                        preFragment.startObserveMatchListChange()
                    }
                }
            }
        }

        if (currentPosition + 1 < (mBinding.layoutContainer.vpGameList.adapter?.itemCount ?: 0)) {
            leaguePagerAdapter?.getItemId(currentPosition + 1)?.also { preItemId ->
                childFragmentManager.findFragmentByTag("f$preItemId").also { preFragment ->
                    if (preFragment is MatchListPagerFragment) {
                        preFragment.startObserveMatchListChange()
                    }
                }
            }
        }
    }


    /**
     * 排序選單的展開收起切換
     * @param expanded : Boolean 展開、收起
     */
    private fun toggleTournamentSorting(expanded: Boolean) {
        val container = mBinding.layoutContainer.llTournamentsDropdown
        isExpanded = expanded

        if (expanded) {
            // 展開排序選單
            if (sortingMenuBinding == null) {
                sortingMenuBinding = LayoutTournamentSortingMenuBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    container,
                    false
                )
                setupSortingMenuViews()
            }

            // 先立即顯示遮罩層遮擋底下內容，避免閃爍
            mBinding.layoutContainer.vTournamentListMask.apply {
                visibility = View.VISIBLE
                alpha = 1f
                // 設置點擊事件
                clickNoRepeat {
                    toggleTournamentSorting(false)
                }
            }

            container.removeAllViews()
            container.addView(sortingMenuBinding?.root)
            container.visibility = View.VISIBLE

            // 立即開始動畫
            val slideInAnim =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_top)
            sortingMenuBinding?.root?.startAnimation(slideInAnim)

            // 切換圖標為收起狀態
            mBinding.layoutContainer.ivTournamentSortIcon.setImageResource(arch.cayenne.lib.common.R.drawable.ic_sort_collapse)

            // tv_tournament_more 變色為選中狀態
            mBinding.layoutContainer.tvTournamentMore.setTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.color_00E0E5
                )
            )

        } else {
            // 收起排序選單 - 使用動畫
            val slideOutAnim = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.slide_out_to_top
            )
            slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    container.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            sortingMenuBinding?.root?.startAnimation(slideOutAnim)

            // 收回時隱藏遮罩層（帶動畫效果）
            mBinding.layoutContainer.vTournamentListMask.animate()
                .alpha(0f)
                .setDuration(defaultAnimDuration)
                .setListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(p0: android.animation.Animator) {}

                    override fun onAnimationEnd(p0: android.animation.Animator) {
                        mBinding.layoutContainer.vTournamentListMask.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: android.animation.Animator) {}
                    override fun onAnimationRepeat(p0: android.animation.Animator) {}
                })
                .start()

            // 切換圖標為展開狀態
            mBinding.layoutContainer.ivTournamentSortIcon.setImageResource(arch.cayenne.lib.common.R.drawable.ic_sort_expand)

            // tv_tournament_more 恢復為未選中狀態
            mBinding.layoutContainer.tvTournamentMore.setTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.color_C0C0C0
                )
            )
        }
    }

    /**
     * 設置排序選單視圖的點擊事件和初始狀態
     */
    private fun setupSortingMenuViews() {
        sortingMenuBinding?.let { binding ->
            // 設置初始選中狀態
            updateSortingMenuSelection()

            // 點擊按熱門排序
            binding.tvSortByHot.clickNoRepeat {
                if (currentSortType != MatchListSortType.BY_HOT) {
                    currentSortType = MatchListSortType.BY_HOT
                    updateSortingMenuSelection()
                    applySorting()
                }
                toggleTournamentSorting(false)
            }

            // 點擊按時間排序
            binding.tvSortByTime.clickNoRepeat {
                if (currentSortType != MatchListSortType.BY_TIME) {
                    currentSortType = MatchListSortType.BY_TIME
                    updateSortingMenuSelection()
                    applySorting()
                }
                toggleTournamentSorting(false)
            }
        }
    }

    /**
     * 更新排序選單的選中狀態
     */
    private fun updateSortingMenuSelection() {
        sortingMenuBinding?.let { binding ->
            val selectedColor = SkinnableResourceManager.getColor(
                requireContext(),
                arch.cayenne.lib.common.R.color.color_00E0E5
            )
            val unselectedColor = SkinnableResourceManager.getColor(
                requireContext(),
                arch.cayenne.lib.common.R.color.color_999999
            )

            when (currentSortType) {
                MatchListSortType.BY_HOT -> {
                    binding.tvSortByHot.setTextColor(selectedColor)
                    binding.tvSortByTime.setTextColor(unselectedColor)
                }

                MatchListSortType.BY_TIME -> {
                    binding.tvSortByHot.setTextColor(unselectedColor)
                    binding.tvSortByTime.setTextColor(selectedColor)
                }
            }
        }
    }

    /**
     * 應用排序邏輯
     */
    private fun applySorting() {
        // TODO: 實現實際的排序邏輯
        // 根據 currentSortType 來決定如何排序賽事列表
        when (currentSortType) {
            MatchListSortType.BY_HOT -> {
                // 按熱門聯賽排序的邏輯
                // 可以調用 ViewModel 的方法來更新數據
            }

            MatchListSortType.BY_TIME -> {
                // 按比賽時間排序的邏輯
                // 可以調用 ViewModel 的方法來更新數據
            }
        }
    }


    private fun getFutureSevenDays() = getFuture31Days().take(7)
    private fun getFuture31Days() = DateUtils.getFutureDays(
        31,
        Locale.getDefault(),
        resources.getString(R.string.first_day_title),
        "M-dd",
        "E"
    )


    private fun setTournamentAndViewPagerLayout(tournamentCombos: List<TournamentCombo>) {

        with(mBinding.layoutContainer) {
            if (leaguePagerAdapter == null) {
                leaguePagerAdapter = LeaguePagerAdapter(
                    fragmentManager = childFragmentManager,
                    lifecycle = viewLifecycleOwner.lifecycle,
                )
                vpGameList.adapter = leaguePagerAdapter
                vpGameList.offsetLeftAndRight(1)
            }
            leaguePagerAdapter!!.setData(
                mViewModel.currentPlayTypeId, tournamentCombos + TournamentCombo(
                    listOf(
                        TournamentDataModel.createAllItem(
                            mViewModel.currentPlayTypeId, mViewModel.currentSportId
                        )
                    ), true
                )
            )

            //因為一開始有觸發resetHome(),觸發resetLiveData()，所以observe livedata tournaments可能會是空的
            //導致tabLayout沒有資料時又多設定一次OnTabSelectedListener，因此要先清除之前的listener
            mBinding.layoutContainer.tlLeagueList.clearOnTabSelectedListeners()

            tournamentTabLayoutMediator?.detach()
            tournamentTabLayoutMediator = CustomTabLayoutMediator(
                tabLayout = tlLeagueList,
                viewPager = vpGameList
            ) { tab, position ->
                tournamentCombos.getOrNull(position)?.let {
                    tab.tag = it
                }
            }.also { layoutMediator ->
                layoutMediator.attach(
                    afterTabSelected = { position ->
                        tournamentCombos.getOrNull(position)?.let { tournamentCombo ->
                            mViewModel.setCurrentTournamentIdList(tournamentCombo.leagueIdList)
                            // 需求3：標記外部tab已切換，下次打開彈窗時需要清空篩選
                            mViewModel.markTournamentTabSwitched()
                            // 需求3：清除按鈕選中狀態和已保存的選中聯賽
                            mViewModel.clearSavedTournamentSelections()
                        }
                        startObservePageMatchListChange(position)
                    }
                )

                if (mViewModel.isAllowTabLoad) {
                    drawTournamentTab()
                }

                if (tournamentCombos.isNotEmpty()) {
                    val selectedPosition =
                        tournamentCombos.indexOfFirst { it.isSelected }
                    tlLeagueList.setScrollPosition(selectedPosition, 0f, true)
                    tlLeagueList.post { layoutMediator.selectTabWithoutAnimation(selectedPosition) }
                    vpGameList.post {
                        gameListPageCallback?.onPageScrollStateChanged(
                            SCROLL_STATE_IDLE
                        )
                    }
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setTopMaskListener() {
        with(mBinding) {
            clSubMain.setOnChildClickedInterceptedListener { view ->
                when (view) {
                    viewSecondNavbar -> {
                    }

                    else -> Unit
                }
            }
            with(layoutContainer) {
                viewContainerRoot.setOnChildClickedInterceptedListener { view ->
                    when (view) {
                        tlContainer -> {
                            lifecycleScope.launch {
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun createTournamentTabView(
        tournamentCombo: TournamentCombo
    ): View {
        val tabBinding =
            ItemLeagueTabBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        tabBinding.apply {
            val tournament = tournamentCombo.tournamentList[0]
            tvLeagueName.text = if (tournament.id == HomeViewModel.TOURNAMENT_ALL_ID)
                getString(R.string.league_all)
            else tournament.simpleName

            if (tournament.id == HomeViewModel.TOURNAMENT_ALL_ID) {
                ivLeagueIcon.visibility = View.GONE
                // 設置「全部」tab 的寬度
                root.layoutParams = LinearLayout.LayoutParams(51.dp2px, 32.dp2px)
            } else {
                Glide.with(this@ChampionSubFragment)
                    .load(tournament.icon.ifEmpty { R.drawable.ic_default_tournament })
                    .placeholder(R.drawable.ic_default_tournament)
                    .error(R.drawable.ic_default_tournament)
                    .into(ivLeagueIcon)
            }
            root.setBackgroundResource(R.drawable.selector_league_tab_bg)
        }
        return tabBinding.root
    }

    private fun enableHorizontalScroll(isEnabled: Boolean) {
        val key = getString(R.string.new_home_vp_sub_key)
        val result = Bundle().apply {
            putBoolean(getString(R.string.key_enable_horizontal_scroll), isEnabled)
        }
        // 使用 parentFragmentManager 發送結果
        parentFragmentManager.setFragmentResult(key, result)
    }

    /**
     * 清除 tlLeagueList 的選中狀態（需求2）
     */
    private fun clearLeagueListSelection() {
        with(mBinding.layoutContainer) {
            // 清除所有 tab 的選中狀態
            val tabLayout = tlLeagueList
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.let { tab ->
                    tab.customView?.isSelected = false
                }
            }
            // 不設置任何 tab 為選中
            tabLayout.selectTab(null)
        }
    }

    /**
     * 更新聯賽按鈕樣式
     * @param hasSelection true: 有選中的聯賽，false: 沒有選中的聯賽
     */
    private fun updateTournamentButtonStyle(hasSelection: Boolean) {
        with(mBinding.layoutContainer) {
            if (hasSelection) {
                // 有選中狀態：高亮顯示
                llBtnTournament.setBackgroundResource(R.drawable.selector_league_tab_bg)
                llBtnTournament.isSelected = true
                tvBtnTournament.setTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.league_tab_tint_select
                    )
                )
                ivBtnTournamentIcon.setImageResource(R.drawable.ic_tournament_list_selected)
            } else {
                // 無選中狀態：默認樣式
                llBtnTournament.setBackgroundResource(R.drawable.shape_tournament_more_bg)
                llBtnTournament.isSelected = false
                tvBtnTournament.setTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
                ivBtnTournamentIcon.setImageResource(R.drawable.ic_tournament_list)
            }
        }
    }

    /**
     * 設置冠軍頁面專屬元件的可見性
     * @param isChampionMode true: 冠軍模式（隱藏今日/早盤元件），false: 今日/早盤模式（顯示元件）
     */
    private fun setChampionModeVisibility(isChampionMode: Boolean) {
        val visibility = if (isChampionMode) View.GONE else View.VISIBLE
        with(mBinding) {
            llBanner.visibility = visibility
            layoutContainer.root.visibility = visibility
            layoutContainer.llBtnTournament.visibility = visibility
            layoutContainer.llTournamentSort.visibility = visibility
            layoutContainer.vLeagueMoreMask.visibility = visibility
        }
    }

    private fun initChampionTournamentLayout() {
        setChampionModeVisibility(true)

        val tournamentListFragment = TournamentListFragment.newInstance(
            playTypeId = PlayType.CHAMPION.id,
            sportId = mViewModel.currentSportId,
            type = TournamentListType.CHAMPION
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_champion_container, tournamentListFragment, PlayType.CHAMPION.name)
            .commit()
    }

    override fun onBackPressed(): Boolean {
        // 如果排序選單展開，先收起排序選單
        if (isExpanded && mViewModel.currentPlayTypeId != PlayType.CHAMPION.id) {
            toggleTournamentSorting(false)
            return true
        }
        return super.onBackPressed()
    }

    override fun onDestroyView() {
        sortingMenuBinding = null
        super.onDestroyView()
    }

    companion object {
        const val LOW_MEMORY_THRESHOLD = 2_000_000_000L
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        fun newInstance(playTypeId: Int): ChampionSubFragment {
            return ChampionSubFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                }
            }
        }
    }

}