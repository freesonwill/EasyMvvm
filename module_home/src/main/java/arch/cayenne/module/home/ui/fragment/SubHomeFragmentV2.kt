package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.adapter.BannerImageMatchAdapter
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.ui.view.WLLinearGradientFontSpan
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.BounceEdgeEffectHelper
import arch.cayenne.lib.common.utils.helper.VIPResourceHelper
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.MatchListSortType
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.TournamentListType
import arch.cayenne.module.home.databinding.FragmentSubHomeV2Binding
import arch.cayenne.module.home.databinding.LayoutTournamentSortingMenuBinding
import arch.cayenne.module.home.ui.adapter.SportsListAdapter
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.SubHomeViewModel
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

class SubHomeFragmentV2 : BaseFragment<SubHomeViewModel, FragmentSubHomeV2Binding>(),
    ISubFragmentLifecycle {

    override val vbClass: KClass<FragmentSubHomeV2Binding> = FragmentSubHomeV2Binding::class
    override val vmClass: KClass<SubHomeViewModel> = SubHomeViewModel::class
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()

    private var allTabCompleteObserveJob: Job? = null
    private var drawSportListJob: Job? = null

    private var isExpanded = false
    private var sortingMenuBinding: LayoutTournamentSortingMenuBinding? = null

    // 當前排序類型，預設為按熱門聯賽排序
    private var currentSortType = MatchListSortType.BY_HOT

    private val defaultAnimDuration = 300L

    private val sportsListAdapter by lazy {
        SportsListAdapter { id ->
            if (mViewModel.currentSportId == id) return@SportsListAdapter
            mViewModel.setCurrentSport(id)
        }
    }
    private var sortMenuClicked: Boolean = false


    // VIP 等級資源設置於 lib_common，統一使用 VIPResourceHelper 管理

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.apply {
            mViewModel.setPlayTypeId(this.getInt(ARG_PLAY_TYPE_ID))
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun initView(savedInstanceState: Bundle?) {
        initSportBanner()
        initSportLayout()
        setupMatchFragment()
        initTournamentLayout()
    }

    override fun initListener() {
        with(mBinding) {
            setTopMaskListener()

            includeSportBanner.vpSportBanner.setOnBannerListener { Int, position ->
                showToast("策划设计中")
            }

            clVipInfo.apply {
                clickNoRepeatSingle { navigate(arch.cayenne.lib.res.R.string.nav_module_vip_fragment.deeplink()) }
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
            sportsStatistical.observeEvent(viewLifecycleOwner, this@SubHomeFragmentV2) {
                tempSportData = it
                if (mViewModel.isAllowTabLoad) {
                    drawSportList()
                }
            }
        }

        mViewModel.tournamentsPlain.observeEvent(viewLifecycleOwner, this) { list ->
            val l = ArrayList<SimpleTabDataModel>()
            list.take(10)
                .forEach { item ->
                    l.add(
                        SimpleTabDataModel(
                            id = item.id,
                            simpleName = item.simpleName,
                            icon = item.icon,
                        )
                    )
                }

            mBinding.layoutContainer.customTabGroup.clearTabList()
            mBinding.layoutContainer.customTabGroup.submitTabList(l)

            setupMatchFragment()
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

        mViewModel.savedTournamentSelections.observe(viewLifecycleOwner) { selections ->
            if (selections.size > 1) {
                // 更新联赛按钮样式为选中状态。
                updateTournamentButtonStyle(true)

                // 清除联赛列表的选中状态。
                mBinding.layoutContainer.customTabGroup.clearLeagueListSelection()

                // 设置当前选中的联赛 ID 列表。
                mViewModel.setCurrentTournamentIdList(selections)
            } else if (selections.size == 1) {
                // 获取当前选中的联赛 ID 在联赛列表中的索引。
                val index = mViewModel.tournamentsPlain.value?.peekContent()
                    ?.indexOfFirst { it.id == selections[0] }

                // 根据索引设置联赛按钮的选中状态
                if (index != null && index in 0..<mBinding.layoutContainer.customTabGroup.tabCount()) {
                    //该分类在外部 tab 中存在, 外部【联赛】按钮状态不亮起, 外部 tab 自动选中该分类 tab
                    // 更新联赛按钮样式为未选中状态。
                    updateTournamentButtonStyle(false)
                    mBinding.layoutContainer.customTabGroup.select(index)
                } else {
                    //该分类不在外部 tab 中, 外部【联赛】按钮状态亮起, 外部 tab 不选中任何联赛
                    updateTournamentButtonStyle(true)
                    // 设置当前选中的联赛 ID 列表。
                    mViewModel.setCurrentTournamentIdList(selections)
                    // 清除联赛列表的选中状态。
                    mBinding.layoutContainer.customTabGroup.clearLeagueListSelection()
                }
            } else {
                // 如果没有其他选中的联赛，此代码将默认选中第一个联赛按钮。
                mBinding.layoutContainer.customTabGroup.select(0)
            }
        }

        // 觀察是否需要清除 tlLeagueList 的選中狀態
        mViewModel.shouldClearLeagueListSelection.observeEvent(viewLifecycleOwner, this) {
            //todo 清除 tlLeagueList 的選中狀態
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
    }

    override fun reloadCurrentMatchListPagerFragment() {
        val fragment =
            childFragmentManager.findFragmentByTag(MatchListPagerFragmentV2.TAG) ?: return
        (fragment as? MatchListPagerFragment)?.reloadAllData()
    }

    // 設置更多按鈕的顯示狀態
    override fun onFragmentUnSelected() {
        mViewModel.requestCollapseTournamentDropdown()
        // 收起排序選單
        if (isExpanded) {
            toggleTournamentSorting(false)
        }
    }


    // init Sport Banner 輪播區塊
    @SuppressLint("ClickableViewAccessibility")
    private fun initSportBanner() {
        val mockBannerList = arrayListOf(
            R.drawable.banner_ad1,
            R.drawable.banner_ad1,
            R.drawable.banner_ad1,
            R.drawable.banner_ad1,
            R.drawable.banner_ad1,
        )

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
            val span = getGradientSpan(levelStr,start,end)
            tvLevel.setText(span, TextView.BufferType.SPANNABLE)
            tvPercent.text = percent
            val color = VIPResourceHelper.getProgressStartColor(level)
            vipProgress.setProgressColor(color)
            vipProgress.setProgress(progress)
            tvLevelUpInfo.text = levelUpInfo
        }
    }

    private fun getGradientSpan(content: String, startColor: Int, endColor: Int): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(content)
        val span = WLLinearGradientFontSpan(startColor, endColor)
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableStringBuilder
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
                        outRect.right = if (pos == last) 0 else 4.dp2px - 1   // marginEnd
                    }
                })
                adapter = sportsListAdapter
            }
        }
    }

    private fun setupMatchFragment() {
        //生成MatchListPagerFragmentV2， 添加到fragment_game_list_container节点
        MatchListPagerFragmentV2.newInstance(
            playTypeId = mViewModel.currentPlayTypeId,
            sportId = mViewModel.currentSportId
        ).also {

            childFragmentManager.beginTransaction()
                .replace(
                    mBinding.layoutContainer.fragmentGameListContainer.id,
                    it,
                    MatchListPagerFragmentV2.TAG
                ).commitNow()
        }
    }

    // 全部Tab的比賽列表載入完成後的處理
    private fun handleAllTabLoaded() {
        allTabCompleteObserveJob = null
        drawSportList()
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

    //init 三級導航欄位與日期，只有今日和早盤有
    @SuppressLint("DefaultLocale")
    private fun initTournamentLayout() {

        with(mBinding.layoutContainer) {
            customTabGroup.setTabClickListener(object :
                arch.cayenne.lib.common.ui.view.CustomGameTabClickListener {
                override fun onTabClicked(id: Int) {
                    //点击全部 取消全部选中
                    if (id == 0) {
                        mViewModel.clearTournamentsSelected()
                    } else {
                        mViewModel.selectTournamentsId(id)
                    }

                    toggleTournamentSorting(false)
                }
            })

            //如果直接點擊聯賽到ViewPager還沒生成的MatchListPageFragment聯賽的話，這個MatchListPageFragment會生成並且attach上去，所以在這裡需要做attach完成後的startObserveMatch
            childFragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    v: View,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    startObservePageMatchListChange()
                }

            }, false)
        }


        mBinding.layoutContainer.customTabGroup.setOnSortBtnClick {
            toggleTournamentSorting(!isExpanded)
        }
        mBinding.layoutContainer.customTabGroup.setOnShowAllCategoryClick({}, {
            // 点击更多供应商按钮时，需要判断排序菜单是否展开，若展开则先收起
            if (isExpanded) {
                toggleTournamentSorting(false)
            }
            showTournamentListBottomSheet()
        })

        setExpandBtnText(getString(R.string.league))
        setSortBtnText()

    }


    /**
     *
     * */
    fun startObservePageMatchListChange() {
        (childFragmentManager.findFragmentByTag(MatchListPagerFragmentV2.TAG) as? MatchListPagerFragmentV2)
            ?.startObserveMatchListChange()

    }


    /**
     * 排序選單的展開收起切換
     * @param expanded : Boolean 展開、收起
     */
    private fun toggleTournamentSorting(expanded: Boolean) {
        isExpanded = expanded
        val container = mBinding.layoutContainer.llTournamentsDropdown

        if (expanded) {
            container.visibility = View.VISIBLE

            // 展開排序選單
            if (sortingMenuBinding == null) {
                sortingMenuBinding = LayoutTournamentSortingMenuBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    container,
                    false
                )
                container.addView(sortingMenuBinding?.root)
                setupSortingMenuViews()
            }

            // 先立即顯示遮罩層遮擋底下內容，避免閃爍
            mBinding.layoutContainer.vGameListMask.apply {
                visibility = View.VISIBLE
                alpha = 1f
                // 設置點擊事件
                clickNoRepeat {
                    toggleTournamentSorting(false)
                }
            }

            // 立即開始動畫
            //TODO: 移除xml動畫，改用程式碼設置動畫屬性
            val slideInAnim =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_top)
            sortingMenuBinding?.root?.startAnimation(slideInAnim)
            slideInAnim.duration =
                AnimationController[AnimType.popupEnter]?.duration ?: defaultAnimDuration
            slideInAnim.interpolator =
                AnimationController[AnimType.popupEnter]?.interpolator?.toInterpolator()
                    ?: LinearInterpolator()


            // 切換圖標為收起狀態
            mBinding.layoutContainer.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_collapse)

            //  變色為選中狀態
            mBinding.layoutContainer.customTabGroup.setSortBtnTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.color_00E0E5
                )
            )

        } else {
            // 收起排序選單 - 使用動畫
            //TODO: 移除xml動畫，改用程式碼設置動畫屬性
            val slideOutAnim = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.slide_out_to_top
            )
            slideOutAnim.duration =
                AnimationController[AnimType.popupExit]?.duration ?: defaultAnimDuration
            slideOutAnim.interpolator =
                AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                    ?: LinearInterpolator()

            slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    container.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            sortingMenuBinding?.root?.startAnimation(slideOutAnim)

            // 收回時隱藏遮罩層（帶動畫效果）
            mBinding.layoutContainer.vGameListMask.animate()
                .alpha(0f)
                .setDuration(
                    AnimationController[AnimType.popupExit]?.duration
                        ?: defaultAnimDuration
                )
                .setListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(p0: android.animation.Animator) {}

                    override fun onAnimationEnd(p0: android.animation.Animator) {
                        mBinding.layoutContainer.vGameListMask.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: android.animation.Animator) {}
                    override fun onAnimationRepeat(p0: android.animation.Animator) {}
                })
                .start()

            if (!sortMenuClicked) {
                mBinding.layoutContainer.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand)
                mBinding.layoutContainer.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
            } else {
                mBinding.layoutContainer.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand_blue)
                mBinding.layoutContainer.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_00E0E5
                    )
                )
            }
        }
    }

    //直接关闭排序菜单， 不要动画
    private fun closeSortingMenu() {
        if (isExpanded) {
            isExpanded = false
            val container = mBinding.layoutContainer.llTournamentsDropdown
            container.visibility = View.GONE
            mBinding.layoutContainer.vGameListMask.apply {
                visibility = View.GONE
                alpha = 0f
            }
            if (!sortMenuClicked) {
                mBinding.layoutContainer.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand)
                mBinding.layoutContainer.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
            } else {
                mBinding.layoutContainer.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand_blue)
                mBinding.layoutContainer.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_00E0E5
                    )
                )
            }
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
                sortMenuClicked = true
                if (currentSortType != MatchListSortType.BY_HOT) {
                    currentSortType = MatchListSortType.BY_HOT
                    updateSortingMenuSelection()
                    setSortBtnText()
                    setSortBtnSelected()
                    mViewModel.setSortType(currentSortType)
                }
                toggleTournamentSorting(false)
            }

            // 點擊按時間排序
            binding.tvSortByTime.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != MatchListSortType.BY_TIME) {
                    currentSortType = MatchListSortType.BY_TIME
                    updateSortingMenuSelection()
                    setSortBtnText()
                    setSortBtnSelected()
                    mViewModel.setSortType(currentSortType)
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

    private fun setSortBtnText() {
        when (currentSortType) {
            MatchListSortType.BY_TIME -> {
                mBinding.layoutContainer.customTabGroup.setSortBtnText(arch.cayenne.lib.common.R.string.custom_tab_time.getString())
            }

            MatchListSortType.BY_HOT -> {
                mBinding.layoutContainer.customTabGroup.setSortBtnText(arch.cayenne.lib.common.R.string.custom_tab_hot.getString())
            }
        }
    }

    private fun setSortBtnSelected() {
        mBinding.layoutContainer.customTabGroup.setSortBtnSelected()
    }

    private fun setExpandBtnText(text: String) {
        mBinding.layoutContainer.customTabGroup.setExpandBtnText(text)
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

        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            mBinding.includeSportBanner.pbSportBanner.stopTriggerJob()
        } else {
            mBinding.includeSportBanner.pbSportBanner.resetTriggerJob()
        }
        super.onHiddenChanged(hidden)

        if (hidden) {
            if (isExpanded) {
                closeSortingMenu()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isExpanded) {
            closeSortingMenu()
        }
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

    /**
     * 更新按鈕樣式
     * @param hasSelection true: 有選中的供应商，false: 沒有選中的供应商
     */
    private fun updateTournamentButtonStyle(hasSelection: Boolean) {
        mBinding.layoutContainer.customTabGroup.updateTournamentButtonStyle(hasSelection)
    }

    companion object {
        const val LOW_MEMORY_THRESHOLD = 2_000_000_000L
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        fun newInstance(playTypeId: Int): SubHomeFragmentV2 {
            return SubHomeFragmentV2().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                }
            }
        }
    }

}