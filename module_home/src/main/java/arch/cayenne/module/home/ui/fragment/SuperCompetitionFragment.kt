package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.utils.ext.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import arch.cayenne.lib.common.ui.view.WLLinearGradientFontSpan
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.VIPResourceHelper
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.BiDirectionalDate
import arch.cayenne.module.home.data.BiDirectionalDateType
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.databinding.FragmentSuperCompetitionBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.SuperCompetitionViewModel
import arch.cayenne.module.home.utils.scrollToPositionWithoutAnim
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.walisport.module.business.common.ui.fragment.BaseBannerLinkFragment
import com.walisport.module.business.common.ui.viewmodel.BaseBannerViewModel
import com.walisport.module.business.common.utils.ext.setGlobalIndicator
import com.walisport.module.business.common.utils.ext.setGlobalBasicConfig
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

/**
 * 超级大赛
 */
class SuperCompetitionFragment :
    BaseBannerLinkFragment<SuperCompetitionViewModel, FragmentSuperCompetitionBinding>(),
    ISubFragmentLifecycle {

    override val vbClass: KClass<FragmentSuperCompetitionBinding> =
        FragmentSuperCompetitionBinding::class
    override val vmClass: KClass<SuperCompetitionViewModel> = SuperCompetitionViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        launch { initSportBanner() }
        initMatchListFragment()
    }


    override fun initListener() {
        super.initListener()
        with(mBinding) {
            clVipInfo.apply {
                clickNoRepeatSingle { navigate(arch.cayenne.lib.res.R.string.nav_module_vip_fragment.deeplink()) }
                addScaleOnTouchAnimation()
            }
        }
    }

    override fun provideBannerViewModel(): BaseBannerViewModel {
        return sharedViewModel<HomeViewModel, NewHomeFragment>().value
    }

    override fun provideBannerAppBarLayout(): AppBarLayout {
        return mBinding.aplHomeBanner
    }

    override suspend fun createObserver() {
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
                    progress = progress
                )
            }
        }
        mViewModel.dateList.observe(viewLifecycleOwner) {
            // 日期 Tab 設定
            updateDateTabs(mBinding.tlDateList, it!!)
            addDateTabListener()
            lifecycleScope.launch {
                mViewModel.selectedDate(it.first().timestamp)
            }
        }

        mViewModel.tournaments.observeEvent(viewLifecycleOwner, this) { comboList ->
            startObservePageMatchListChange()
        }

        mViewModel.displayDate.observe(viewLifecycleOwner) { display ->
            if (display.timestamp == HomeViewModel.DEFAULT_DATE) return@observe
            showSelectedDateTab(mViewModel.getDisplayDateIndex(display.timestamp))
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setCurrentSport(arguments?.getInt(ARG_SPORT_ID) ?: SportType.SOCCER.id)
    }

    // init Sport Banner 輪播區塊
    @SuppressLint("ClickableViewAccessibility")
    private suspend fun initSportBanner() {
        val mockBannerList = mViewModel.getBannerList()
        with(mBinding.includeSportBanner) {
            val adapter = BannerImageMatchAdapter(mockBannerList)
            vpSportBanner.setAdapter(adapter)
            vpSportBanner.setGlobalBasicConfig()
            vpSportBanner.setGlobalIndicator()
            vpSportBanner.start()
        }
    }

    private fun updateVIPInfo(
        vipLevel: Int,
        vipStage: Int,
        percent: String,
        levelUpInfo: String,
        progress: Float
    ) {
        // 使用 VIPResourceHelper 轉換等級
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

    private fun addDateTabListener() {
        mBinding.tlDateList.addOnTabSelectedListener2(object :
            TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                playFadeAnimTriggerByDateTab {
                    launch {
                        mViewModel.selectedDate(
                            mViewModel.dateList.value?.find { it.dateStr == tab.tag }?.timestamp
                                ?: return@launch
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {}
            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {}
        })
    }

    private fun clearDateTabSelection() {
        val tabLayout = mBinding.tlDateList
        tabLayout.setScrollPosition(0, 0f, true)
        val tabStrip = tabLayout.getChildAt(0) as? LinearLayout ?: return
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i)?.isSelected = false
            tabLayout.getTabAt(i)?.customView?.isSelected = false
        }
        tabLayout.selectTab(null)
    }

    private fun resetDateTabs() {
//        mBinding.layoutContainer.tvTabAll.isSelected = true
        clearDateTabSelection()
    }

    private fun createDateTab(
        date: String?,
        weekday: String?,
        type: BiDirectionalDateType
    ): TabLayout.Tab {
        val tab = mBinding.tlDateList.newTab()
        val tabView = ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false).apply {
            if (type == BiDirectionalDateType.Date) {
                tvDate.visibility = View.VISIBLE
                tvDate.text = date
                tvWeekDay.visibility = View.VISIBLE
                tvWeekDay.text = weekday

                tvOther.visibility = View.GONE
            } else {
                tvDate.visibility = View.GONE
                tvWeekDay.visibility = View.GONE

                tvOther.visibility = View.VISIBLE
                tvOther.text = date
            }


        }
        tab.customView = tabView.root
        tab.tag = date
        return tab
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun showSelectedDateTab(indexOfTabs: Int?) {
        with(mBinding) {
            if (indexOfTabs == null || indexOfTabs == -1) {
                resetDateTabs()
                return
            }

            if (indexOfTabs != -1) {
                tlDateList.post {
                    val tabStrip = tlDateList.getChildAt(0) as LinearLayout
                    for (i in 0 until tabStrip.childCount) {
                        tabStrip.getChildAt(i).apply {
                            isSelected = i == indexOfTabs
                        }
                    }
                }

                //scroll smooth
                val tab: TabLayout.Tab? = tlDateList.getTabAt(indexOfTabs)
                if (tab?.view == null) return

                val tabView: View = tab.view
                tabView.post {
                    val screenWidth: Int = tlDateList.width
                    val tabWidth = tabView.width
                    val tabLeft = tabView.left
                    val tabCenter = tabLeft + tabWidth / 2
                    var targetScrollX = tabCenter - screenWidth / 2

                    // 限制滚动范围，防止越界
                    val maxScrollX: Int = tlDateList.getChildAt(0).width - screenWidth
                    targetScrollX =
                        max(0.0, min(targetScrollX.toDouble(), maxScrollX.toDouble()))
                            .toInt()
                    // 使用平滑滚动
                    tlDateList.smoothScrollTo(targetScrollX, 0)
                }
            }
        }
    }

    private fun updateDateTabs(
        tlDateList: TabLayout,
        dateTabs: List<BiDirectionalDate>
    ) {
        tlDateList.apply {
            removeAllTabs()
            dateTabs.forEach { (date, weekday, _, type) ->
                val tab = createDateTab(date, weekday, type)
                addTab(tab)
            }
            setupDateTabLayoutParams(tlDateList, true)
        }
    }

    private fun setupDateTabLayoutParams(tabLayout: TabLayout, clearSelected: Boolean) {
        tabLayout.post {
            val tabStrip = tabLayout.getChildAt(0) as LinearLayout
            for (i in 0 until tabStrip.childCount) {
                tabStrip.getChildAt(i).apply {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.width = 52.dp2px
                    params.height = 37.dp2px
                    params.marginStart = 3.dp2px
                    params.marginEnd = 4.dp2px
                    layoutParams = params
                    setBackgroundResource(R.drawable.selector_date_tab_bg)
                    if (clearSelected) isSelected = false
                }
            }
        }
    }

    private fun playFadeAnimTriggerByDateTab(switchProcess: () -> Unit) {
        mBinding.fragmentMatchList.startFadeAnim { onComplete ->
            switchProcess.invoke()
            onComplete.invoke()
        }
    }

    private fun initMatchListFragment() {
        childFragmentManager.findFragmentByTag(BiDirectionalMatchListPagerFragment.TAG) as? BiDirectionalMatchListPagerFragment
            ?: BiDirectionalMatchListPagerFragment.newInstance(
                SportType.SOCCER.id,
                PlayType.SUPER_COMPETITION.id,
                arguments?.getIntArray(ARG_LEAGUE_ID)?.toList() ?: listOf(0),
                0,
                false
            ).also {
                childFragmentManager.beginTransaction()
                    .replace(
                        mBinding.fragmentMatchList.id,
                        it,
                        BiDirectionalMatchListPagerFragment.TAG
                    )
                    .commitNow()
            }

    }


    /**
     *
     * */
    private fun startObservePageMatchListChange() {
        val fragment =
            childFragmentManager.findFragmentByTag(BiDirectionalMatchListPagerFragment.TAG)
                ?: return
        if (fragment is BiDirectionalMatchListPagerFragment) {
            fragment.startObserveMatchListChange()
        }
    }

    override fun onFragmentSelected() {
        mBinding.tlDateList.scrollToPositionWithoutAnim(mBinding.tlDateList.selectedTabPosition)
    }

    override fun onFragmentUnSelected() {
    }

    override fun reloadCurrentMatchListPagerFragment() {
        val itemId = 0
        val fragment = childFragmentManager.findFragmentByTag("f$itemId") ?: return
        (fragment as? BiDirectionalMatchListPagerFragment)?.reloadAllData()
    }

    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_LEAGUE_ID = "arg_league_id"

        fun newInstance(sportId: Int, leagueIdList: List<Int>): SuperCompetitionFragment {
            return SuperCompetitionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                    putIntArray(ARG_LEAGUE_ID, leagueIdList.toIntArray())
                }
            }
        }
    }
}


